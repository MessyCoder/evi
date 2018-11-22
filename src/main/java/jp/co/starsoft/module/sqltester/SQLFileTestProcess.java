package jp.co.starsoft.module.sqltester;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SQLFileTestProcess {

    private static final long serialVersionUID = 1L;

    private static final char ESC_CHAR = '\\';
    private static final char DELIMITER_CHAR = '=';
    private static final char COMMENT_CHAR = '#';

    private List<String> disorderedSQLID = new ArrayList<>();
    private List<String> non9bitsSQLID = new ArrayList<>();

    private List<SQLEntry> sqlEntries = new ArrayList<>();

    public SQLFileTestProcess() {

    }

    public Collection<String> getDisorderedSQLID() {
        return Collections.unmodifiableCollection(disorderedSQLID);
    }

    public Collection<String> getNon9bitsSQLID() {
        return Collections.unmodifiableCollection(non9bitsSQLID);
    }

    /**
     * 読込んだSQLには問題ないかを確認する。
     */
    private void add(SQLEntry sqlEntry) {

        String id = sqlEntry.getId();
        if (id.length() != 9) {
            non9bitsSQLID.add(id);
            sqlEntries.add(sqlEntry);
            return;
        }

        if (sqlEntries.size() > 0) {
            String lastId = sqlEntries.get(sqlEntries.size() - 1).getId();
            if (lastId.compareTo(id) > 0) {
                disorderedSQLID.add(id);
            }
        }
        sqlEntries.add(sqlEntry);
        return;
    }

    /**
     * Streamから読み込む
     *
     * @param is the InputStream
     * @throws Exception 例外
     */
    public void load(InputStream is) throws Exception {
        //Copied from jp.co.tis.cc.ss99.fw.SQLReader

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String sentence;
            while (null != (sentence = _readSentence(reader))) {

                StringBuilder sqlID = new StringBuilder(80); // Key
                StringBuilder sqlContent = new StringBuilder(80); // Value


                int i = 0;
                boolean isKeyOk = false;
                for (; i < sentence.length(); i++) {
                    if (sentence.charAt(i) == ESC_CHAR) {
                        sqlID.append(sentence.charAt(++i));
                    } else if (sentence.charAt(i) == DELIMITER_CHAR) {
                        if (sqlID.toString().trim().length() != 0) {
                            isKeyOk = true;
                        }
                        break;
                    } else {
                        sqlID.append(sentence.charAt(i));
                    }
                }

                if (!isKeyOk) {

                    continue;
                }

                boolean space = false;
                boolean literal = false;
                for (i++; i < sentence.length(); i++) {

                    char c;
                    if (sentence.charAt(i) == ESC_CHAR) {
                        c = sentence.charAt(++i);
                    } else if (sentence.charAt(i) == DELIMITER_CHAR) {
                        break;
                    } else {
                        c = sentence.charAt(i);
                    }

                    if (Character.isWhitespace(c)) {

                        if (!space || literal) {

                            if (c == 0x09 && !literal) {

                                sqlContent.append(' ');
                            } else {
                                sqlContent.append(c);
                            }
                        }
                        space = true;
                    } else {

                        space = false;
                        sqlContent.append(c);
                        if (c == 0x27) {
                            if (literal) {
                                if ((i + 1) == sentence.length()) {
                                    continue;
                                }
                                if (sentence.charAt(i + 1) == 0x27) {
                                    sqlContent.append(sentence.charAt(++i));
                                } else {
                                    literal = false;
                                }
                            } else {
                                literal = true;
                            }
                        }
                    }
                }

                this.add(new SQLEntry(
                        sqlID.toString().trim(),
                        sqlContent.toString().trim())
                );

            }
        }
    }

    private String _readSentence(BufferedReader reader) throws Exception {
        String line;
        StringBuilder sentence = new StringBuilder(160);
        while (null != (line = reader.readLine())) {
            String trimLine = line.trim();

            for (int i = 0; i < trimLine.length(); i++) {
                if (trimLine.charAt(i) == ESC_CHAR) {
                    i++;
                } else if (trimLine.charAt(i) == COMMENT_CHAR) {
                    trimLine = trimLine.substring(0, i).trim();
                    break;
                }
            }

            int escCnt = 0;
            for (int i = trimLine.length() - 1; i >= 0; i--, escCnt++) {
                if (trimLine.charAt(i) != ESC_CHAR) {
                    break;
                }
            }
            if (escCnt % 2 == 1) {
                sentence.append(trimLine.substring(0, trimLine.length() - 1));
                continue;
            }
            sentence.append(trimLine);
            break;
        }
        return line == null ? null : sentence.toString();
    }

    public void runTest() {
        LocalDBAccessor localDBAccessor = new LocalDBAccessor();
        for (SQLEntry sqlEntry : sqlEntries) {

            String sqlString = sqlEntry.getContent();
            try{
                localDBAccessor.dummyRun(sqlString
                        .replaceAll("'\\$\\d*'", "?")
                        .replaceAll("\\$\\d*", "?"));

            } catch(SQLException ex) {
                //mark this sql as NG.
                sqlEntry.testNG(ex.getMessage());
            }
        }

        localDBAccessor.terminate();
    }


    public List<SQLEntry> getReport() {
        return this.sqlEntries;
    }

}
