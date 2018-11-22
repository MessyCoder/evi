package jp.co.starsoft.module.sqltester;

public class SQLEntry {
    private String id;
    private String content;
    private String oracleMessage;

    SQLEntry (String id, String content){
        this.id = id;
        this.content = content;

    }
    public String getOracleMessage() {
        return oracleMessage;
    }
    public String getId() {
        return id;
    }
    public String getContent() {
        return content;
    }

    public void testNG(String message){
        this.oracleMessage = message;
    }
    
}