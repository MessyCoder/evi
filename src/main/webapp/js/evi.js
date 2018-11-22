$(function () {
    var sheet_list = $(".sheet-list");
    var sheet_list_ul = $(".sheet-list-ul");
    sheet_list_ul.selectable();

    var imgColumn = $("#image-display");

    imgColumn.sortable({
        connectWith: "#image-display",
        handle: ".portlet-header",
        cancel: ".portlet-toggle",
        placeholder: "portlet-placeholder ui-corner-all"
    });

    var keydown;
    var addSheetIcon = $("#add_sheet");
    addSheetIcon.on("click", function () {

       var li = $("<li><span class='ui-icon ui-icon-image' style='margin-right: 5px'></span></li>");
       var inputFrame = $("<div class='input-frame'></div>");
       var imageSpan = $("<span class='ui-icon ui-icon-image'></span>").on("click", function () {
           alert(this.className);
       });
       inputFrame.append(imageSpan);

       var input = $("<input type='text'>").on("blur", function () {

           var sheetName = input.val().trim();
           if (sheetName === "") {
               //li.remove();
           } else {
               inputFrame.remove();
               sheet_list_ul.append(li.append(sheetName));
               sheet_list_ul.find("li").removeClass("ui-selected");
               li.addClass("ui-selected");
           }
       }).on("keydown", function(e){
            keydown = e.keyCode;
        }).on("keyup", function (e) {
           if (e.keyCode === 27) {
               // ESC
               inputFrame.remove();
               return;
           } else if (e.keyCode === 13 && 13 == keydown) {

               $.each(e, function(name){
                   console.log(name + " : " + e[name]);
               });
               // Enter
               var sheetName = input.val().trim();
               if (sheetName === "") {
                   //li.remove();
               } else {
                   inputFrame.remove();
                   sheet_list_ul.append(li.append(sheetName));
                   sheet_list_ul.find("li").removeClass("ui-selected");
                   li.addClass("ui-selected");
               }
           }
           console.log(e.keyCode);
       });


        sheet_list_ul.append(inputFrame.append(input));
        input.focus();

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
                "evidence": selectedValue
            },
            success: function(j_data){

                //var sheet_list_ul = $(".sheet-list-ul");
                $.each(j_data, function (i) {

                    sheet_list_ul.append($("<li>").text(j_data[i]));

                });
            }
        });

    });

});


window.addEventListener("paste", function (pasteEvent) {
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
                "image_data": fileReader.result.replace(/^data:image\/(png|jpg);base64,/, "")
            },
            success: function(j_data){
                var img = new Image();
                img.onload = function(){
                    console.log(this.width);
                    console.log(this.height);

                    if (this.width > 1000) {
                        this.width = 1000;
                    }
                    //this.width *= 0.85;
                    //this.height /= 2;

                };
                img.src = "data:image/png;base64," + j_data;

                var imgPortlet = $("<div class='portlet ui-widget ui-widget-content ui-helper-clearfix ui-corner-all'/>")
                    .append($("<div class='portlet-header ui-widget-header ui-corner-all'>Feeds</div>")
                        .prepend($("<span class='ui-icon ui-icon-closethick portlet-toggle'></span>").on( "click", function() {
                            var icon = $(this);
                            // icon.toggleClass( "ui-icon-minusthick ui-icon-plusthick" );
                            // icon.closest( ".portlet" ).find( ".portlet-content" ).toggle();
                            //imgPortlet.remove();
                            imgPortlet.animate({
                                height: 0,
                                opacity: 0
                            }, 200, function () {
                                imgPortlet.remove();
                            });
                        }))
                    )
                    .append($("<div class='portlet-content'></div>").append(img));

                var imgColumn = $("#image-display");
                imgColumn.append(imgPortlet);
                imgColumn.sortable('refresh');

            }
        });
    };
    fileReader.readAsDataURL(imageBlob);

}, false);

function postImageData(dataUrl) {
    //console.log(dataUrl);

    $.ajax({
        type: "POST",
        url: "upload_image",
        data: {
            "image_data": dataUrl.replace(/^data:image\/(png|jpg);base64,/, "")
        },
        success: function(j_data){

            console.log(j_data);

        }
    });
}