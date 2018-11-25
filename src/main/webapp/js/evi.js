$(function () {
    var sheet_list_ul = $(".sheet-list-ul");
    sheet_list_ul.selectable({
        selected: function(event, ui) {
            $(ui.selected).addClass("ui-selected").siblings().removeClass("ui-selected");
        },
        stop: function() {
            var selected = $( ".ui-selected", this );

            if (selected.length !== 1) {
                return;
            }

            var entry = selected.data("entry");

            if (entry.cate === "0") {
                return;
            }


            var imageDisplay = $("#image-display");
            imageDisplay.empty();

            $.ajax({
                type: "GET", url: "upload_image",
                data: {
                    "evidence": $(".evidence-file-list").val(),
                    "sheetName": entry.title
                },
                success: function(j_data){

                    var imageCount = 0;
                    $.each(j_data, function (displayName) {

                        imageCount++;
                        var imageData = j_data[displayName];
                        var img = createDataImage("data:image/png;base64," + imageData, displayName);

                        var imgColumn = $("#image-display");
                        imgColumn.append($("<div class='portlet'>").append(img));

                        img.on("dblclick", function (e) {

                            var diag = $("<div style='z-index:999' title='画像確認'>");

                            var toolbar = $("<div class='tool-bar'>");
                            toolbar.append($("<button>").text("□").button({
                                "icon": "ui-icon-newwin",
                                "showLabel": false
                            }).css("outline", "none").on("click", function () {
                                var fixedStrokeWidth = 3;

                                var rect = new fabric.Rect({
                                    left: 50,
                                    top: 50,
                                    fill: 'transparent',
                                    stroke: "red",
                                    strokeWidth: fixedStrokeWidth,
                                    width: 200,
                                    height: 40,
                                    borderScaleFactor: 1,
                                    borderOpacityWhenMoving: 1,
                                    // borderColor: 'red'
                                    // angle: 45
                                    //opacity: 0.7
                                });

                                rect.setControlsVisibility({
                                    mt: false, // middle top disable
                                    mb: false, // middle bottom
                                    ml: false, // middle left
                                    mr: false, // I think you get it
                                    mtr: false
                                });
                                var canvas = diag.data("canvas");
                                canvas.add(rect);
                                canvas.renderAll();
                            }));
                            diag.append($("<div class='tool-bar-wrapper'>").append(toolbar));



                            var setWidth = (img.data("originWidth") > 1200 ? 1200 : img.data("originWidth")) + 50;
                            var setHeight = (img.data("originHeight") > 700 ? 700 : img.data("originHeight")) + 150;

                            diag.dialog({
                                modal: true,
                                width: setWidth,
                                height: setHeight,
                                position: {
                                    my: "center",
                                    at: "top",
                                    of: window
                                },
                                close: function() {
                                  diag.dialog("destroy");
                                },
                                buttons: {
                                    Ok: function() {
                                        $( this ).dialog( "close" );
                                    }
                                },
                                open: function (event, ui) {
                                    $.ajax({
                                        type: "GET", url: "fabric_image",
                                        data: {
                                            "evidence": $(".evidence-file-list").val(),
                                            "sheetName": entry.title,
                                            "displayName": img.data("displayName")
                                        },
                                        success: function (j_data) {
                                            loadFabricCanvas(diag, "data:image/png;base64," + j_data.base64, img);
                                        }
                                    });
                                }
                                
                            });


                        });


                    });


                }
            });
        }
    });

    $("div.operation-bar .ui-icon").on("click", function () {

        if (checkEvidenceSpecified()) {
            return;
        }

        $("li", sheet_list_ul).removeClass("ui-selected");

        var detail = {};

        if (this.id === "add_image_sheet") {
            detail.icon = "ui-icon-image";
            detail.requestId = "2";
            detail.onFinished = function (sheetName) {

                sheet_list_ul.append(
                    $("<li class='ui-selected'></li>")
                        .append($("<span class='ui-icon' style='margin-right: 5px'></span>").addClass(detail.icon))
                        .append(sheetName).data("entry", {
                            cate: "1",
                            title: sheetName
                    }));
            }
        } else if (this.id === "add_db_sheet") {
            detail.icon = "ui-icon-calculator";
            detail.requestId = "3";
        } else if (this.id === "add_separator_sheet"){
            detail.icon = "ui-icon-grip-dotted-horizontal";
            detail.requestId = "4";
            detail.onFinished = function (sheetName) {
                sheet_list_ul.append($("<li class='day-changing-separator'></li>").append(sheetName).data("entry", {
                    cate: "0",
                    title: sheetName
                }));
            }
        }

        if (detail.requestId) {
            showSheetNameInputFrame(detail, sheet_list_ul);
        }

    });


    $(".evidence-file-list").on("change", function (e) {
        sheet_list_ul.empty();
        var selectedValue = this.value;
        if (!selectedValue) {
            return;
        }

        $.ajax({
            type: "POST", url: "evidence",
            data: {
                "requestId": "1",
                "evidence": selectedValue
            },
            success: function(j_data){

                //var sheet_list_ul = $(".sheet-list-ul");
                $.each(j_data, function (i) {
                    var entry = {
                        cate: j_data[i][0],
                        title: j_data[i].substr(1)
                    };

                    var listItem = $("<li>").data("entry", entry);
                    if (entry.cate === "1") {
                        listItem.append($("<span class='ui-icon ui-icon-image' style='margin-right: 5px'>"))
                            .append(entry.title);
                    } else if (entry.cate === "0") {
                        listItem.addClass("day-changing-separator").append(entry.title);

                    }
                    sheet_list_ul.append(listItem);
                });
            }
        });

    });

});


window.addEventListener("paste", function (pasteEvent) {

    var evidence_file_list = $(".evidence-file-list");
    var evidenceFileName = evidence_file_list.val();

    if (!evidenceFileName) {
        evidence_file_list.effect("shake");
        return;
    }

    var selected = $(".sheet-list-ul li.ui-selected");

    if(selected.length !== 1) {
        return;
    }

    var entry = selected.data("entry");

    if (!entry || entry.cate !== "1") {
        return;
    }

    var items = pasteEvent.clipboardData.items;
    if (items.length === 0) {
        return;
    }
    var lastItem = items[items.length - 1];

    if (lastItem.type.indexOf("image") === -1) {
        return;
    }

    var imageBlob = lastItem.getAsFile();
    var fileReader = new FileReader();
    fileReader.onload = function (ev) {


        $.ajax({
            type: "POST", url: "upload_image",
            data: {
                "evidence": evidenceFileName,
                "sheetName": entry.title,
                "image_data": fileReader.result.replace(/^data:image\/(png|jpg);base64,/, "")
            },
            success: function(response){
                var img = createDataImage(fileReader.result.toString(), response.displayName);

                var imgColumn = $("#image-display");
                imgColumn.append(imgColumn.append($("<div class='portlet'>").append(img)));
            }
        });
    };
    fileReader.readAsDataURL(imageBlob);

}, false);

function createDataImage(dataUrl, displayName) {
    var img = $(new Image());
    img.data("displayName", displayName);
    img.on("load", function(){
        img.data("originWidth", this.width);
        img.data("originHeight", this.height);

        if (this.width > 1000) {
            this.width = 1000;
        }
    });

    img.attr("src", dataUrl);

    return img;
}

function showSheetNameInputFrame(detail, parent) {

    var inputFrame = $("<div class='cancel'>");
    var input = $("<input type='text'>");


    if (detail.requestId !== "4") {
        var imageSpan = $("<span class='ui-icon " + detail.icon + "'></span>");
        inputFrame.append(imageSpan);
        inputFrame.addClass("input-frame-common-text")

    }

    inputFrame.append(input);
    parent.append(inputFrame);
    input.focus();


    var day;
    var night;
    if (detail.requestId === "4") {
        inputFrame.addClass("input-frame-date-only-text");
        input.prop("maxlength", "8");
        day = $("<input type='radio' name='day_night_radio' checked='checked'>");
        night = $("<input type='radio' name='day_night_radio'>");

        inputFrame.append($("<label>日中</label>").append(day));
        inputFrame.append($("<label>夜間</label>").append(night));
    }

    var keyDown;
    input.on("keydown", function(e){
        keyDown = e.keyCode;
    }).on("keyup", function (e) {
        if (e.keyCode === 27) {
            // ESC
            inputFrame.remove();
        } else if (e.keyCode === 13 && 13 === keyDown) {
            var sheetName = input.val().trim();


            if (!sheetName) {
                inputFrame.remove();
                return;
            }

            if(/[,.\\!"#$%&'@\[\]*:;/ 　\-=^~|]/.test(sheetName)) {
                console.log("シート名に不正な記号がふくまれている。");
                inputFrame.css("border","1px solid red");
                inputFrame.effect("shake");
                return;
            }

            var evidence_file_list = $(".evidence-file-list");
            var evidenceFileName = evidence_file_list.val();

            if (!evidenceFileName){
                evidence_file_list.effect("shake");
                return;
            }



            if (detail.requestId === "4") {

                if(!/^\d{8}/.test(sheetName)) {
                    inputFrame.effect("shake");
                    return;
                }

                if(day.prop("checked")) {
                    sheetName += "（日中）"
                } else {
                    sheetName += "（夜間）"
                }
            }


            input.prop('disabled', 'disabled');
            $.ajax({
                type: "POST", url: "evidence",
                data: {
                    "requestId": detail.requestId,
                    "evidence": evidenceFileName,
                    "sheetName": sheetName
                },
                success: function(result){
                    input.prop('disabled', false);
                    if(result.message === "ok") {
                        inputFrame.remove();
                        detail.onFinished(sheetName);
                    } else {
                        console.log("サーバーエラー： " + result.message);
                        inputFrame.css("border","1px solid red");
                        inputFrame.effect("shake");
                        input.focus();
                    }
                }
            });
        }
    });
}

function checkEvidenceSpecified() {
    var evidence_file_list = $(".evidence-file-list");
    var evidenceFileName = evidence_file_list.val();

    if (!evidenceFileName) {
        evidence_file_list.effect("shake");
        return true;
    }
}

function loadFabricCanvas(parentDiv, imageDataUrl, targetImage) {
    parentDiv.append($("<canvas id='canvas'>"));
    var originalImage = $("<img>").attr("src", imageDataUrl).css("display", "none");

    parentDiv.append(originalImage);
    var canvas = new fabric.Canvas("canvas", {
        width: targetImage.data("originWidth"),
        height: targetImage.data("originHeight")
    });
    canvas.uniScaleTransform = true;
    canvas.selection = false;

    canvas.on({
        "object:scaling": function (e) {
            var obj = e.target;
            var realWidth = obj.width * obj.scaleX;
            var realHeight = obj.height * obj.scaleY;

            obj.set("strokeWidth", 0);
            obj.set("width", realWidth);
            obj.set("height", realHeight);
            obj.set("scaleX", 1);
            obj.set("scaleY", 1);
            canvas.renderAll();
        }
    });

    canvas.on({
        "object:scaled": function (e) {
            var obj = e.target;
            obj.set("strokeWidth", 3);
            canvas.renderAll();
        }
    });

    var imgInstance = new fabric.Image(originalImage.get(0));
    originalImage.remove();

    imgInstance.set("selectable", false);
    canvas.add(imgInstance);

    parentDiv.data("canvas", canvas);
}