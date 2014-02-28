/*
var source_to_target = {};
var source_target_to_meta = {};
var jsonObj = [];
var topNObj = [];
*/
jQuery(function($) {
       loadPage("EN");
//    function shout(st) {alert(st);}
       $("#s_box_m").keypress(function(event) {
                if(event.which == 13) {
                var query = $("#s_box_m").val();
                $("#line_c_1").html(query);
                Displayer.randomQuery(query);
                }
        });
       $("#selected_method").change(function() {
                                    DataItem.method = $("#selected_method").val();
                                    if(DataItem.method == "source") {
                                    Displayer.clean_page(0);
                                    Pager.setView("#source", "cell small_cell source_cell");
                                    Pager.showPage(1);
                                    }
                                    else {
                                    Displayer.clean_page(0);
                                    Pager.setView("#metaphor_list", "long_cell metaphor_cell");
                                    Pager.showPage(1);
                                    }
                                    });
});

function loadPage(lang)
{
    Displayer.clean_page(0);
    DataItem.lang = lang;
    DataItem.method = $("#selected_method").val();
    var areaID = DataItem.method == "metaphor" ? "#metaphor_list" : "#source";
    var cssClass = DataItem.method == "metaphor" ? "long_cell metaphor_cell" : "cell small_cell source_cell";
    Pager.setView(areaID, cssClass);
    Pager.showPage(1);

}