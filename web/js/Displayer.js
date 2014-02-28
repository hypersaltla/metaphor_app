/* A display controller that is listening user's clicks and show the data */

var Displayer = new function() { //Singleton pattern because we need only one Object
    
    this.clean_page = function(degree) {
        if(degree == 0) {
            $("#metaphor_list").empty();
            $("#source").empty();
        }
        if(degree <= 1) {
            $("#targets").empty();
            $("#cur_source").html("Source");
        }
        if(degree <= 2) {
            $("#metaphors").empty();
            $("#cur_target").html("Target");
        }
        for(var i = 1; i <= 4; i++) {
            $("#line_c_" + i.toString()).empty();
        }
        $("#emotion_section").empty();
        $("#domain_statistics").empty();
        $("#domain_statistics").css({"height":"0px"});
        $("#topn_content").empty();
    }
    
    this.popTarget = function(key) {
        this.clean_page(1); //with degree = 1, most thourough clean
        key = key.replace(/#/g, ' ');
        //  alert(key);
        $("#cur_source").html(key);
        $("#targets").html("<div class=\"small_cell title_cell\">target</div>");
        var targets = [];
        for(var target in DataItem.sourceToTarget[key]) {
            var metaphorList = DataItem.sourceToTarget[key][target].join();
            $("#targets").append("<div class=\"small_cell target_cell clickable\" onclick=Displayer.popMetaphor(\'" + target.replace(/\s/g, "#") + "\',\'" + metaphorList + "\') >" + target + "</div>");
        }
    }
    
    this.popMetaphor = function(key, list) {
        this.clean_page(2); //second level clean
        //console.log(key); console.log(list);
        key = key.replace(/#/g, ' ');
        $("#cur_target").html(key);
        $.get("main", {"type": "fetch", "method": "metaphor", "ids": list, "lang": DataItem.lang}, function(data, status) {
              DataItem.totalSize = data.total_size;
              DataItem.itemList = data.items;
              DataItem.idList = data.id_list;
              
              $("#metaphors").html("<div class=\"small_cell title_cell\">metaphor</div>");
              for(var i = 0; i < DataItem.itemList.length; i++) {
                //var metaphor_sent = DataItem.itemList[i].raw_metaphor.replace(new RegExp('(' + DataItem.itemList[i].source + ')', 'gi'), "<b>$1</b>");
                //metaphor_sent = metaphor_sent.replace(new RegExp('(' + DataItem.itemList[i].target + ')', 'gi'), "<b>$1</b");
                $("#metaphors").append("<div class=\"small_cell target_cell clickable\" onclick=Displayer.popDetail(1," + i + ") >"
                                       + DataItem.itemList[i].raw_metaphor + "</div>");
              }
        });
    }

    this.popDetail = function(type, id) {
        this.clean_page(3);
        //    alert(id);
        var n_id = Number(id);
        //       $("#cur_source").empty();
        //     $("#cur_target").empty();
        //   $("#line_c_1").empty();
        //      $("#line_c_2").empty();
        //    $("#line_c_3").empty();
        //  $("#line_c_4").empty();
        type = Number(type);
        var item = type ? DataItem.itemList[n_id] : DataItem.topNList[n_id];
        
        $("#line_c_1").html(item.raw_metaphor);
        $("#line_c_2").html(this._getSentiVal(item));
        
        
        emotion_list = ("emotions:value " + item.emotions).split(" ");
        emotion_list[0] = emotion_list[0].split(":");
        for(var i = 1; i < emotion_list.length; i++) {
            emotion_list[i] = emotion_list[i].split(":");
            if(emotion_list[i].length != 2) continue;
            emotion_list[i][1] = parseFloat(emotion_list[i][1]);
        }
        //console.log(emotion_list);
        google.load("visualization", "1", {packages:["corechart"]});
        var data = google.visualization.arrayToDataTable(emotion_list);
        var options = {
            title: 'Emotion Chart'
        };
        var chart = new google.visualization.PieChart(document.getElementById("emotion_section"));
        chart.draw(data, options);
                /*
         var domains = "";
         for(var d in jsonObj[n_id].domains) {
         domains += "<span>" + d + "</span>&nbsp;";
         }
         $("#line_c_3").html(domains);*/
        $("#line_c_3").html("<div class=\"small_cell\"><span><b>Domains:&nbsp;</b></span></div>");
        for(var d in item.domains) {
            $("#line_c_3").append("<div class=\"cell small_cell\" style=\"cursor:pointer\" onclick=Displayer.domainStat(\'" + d.replace(/\s/g, "#") + "\')>" + d + "</div>");
        }
        $("#line_c_4").html("<span onclick=Displayer.popTopN(\'" + item.metaphor.replace(/\s/g, "#") + "\') ><b>topN</b></span>");
        //$("#domains").append("<div class=\"empty\"></div>");
    }
    
    this.domainStat = function(domain)
    {
        domain = domain.replace(/#/g, ' ');
        $("#domain_statistics").empty();
        $("#domain_statistics").css({'height':'200px'});
        $.get("main", {"type": "domain", "domain": domain, "lang": DataItem.lang}, function(data, status) {
              if(status == "success") {
              var my_arr = eval(data);
              var dataTable = [['valence scores', 'metaphor count']];
              for(var i = 0; i < 7; i++) {
              dataTable.push([(i-3).toString(), my_arr[i]]);
              //         console.log(dataTable[i+1]);
              }
              //       console.log(dataTable);
              var data = google.visualization.arrayToDataTable(dataTable);
              var options = {
              title: "domain-valence distribution for \"" + domain + "\"",
              hAxis: {title: 'valence scores', titleTextStyle: {color: 'red'}}
              };
              var chart = new google.visualization.ColumnChart(document.getElementById("domain_statistics"));
              chart.draw(data, options);
              }
              else {
              $("#domain_statistics").html("Empty Result");
              }
        });
    }
    
    this.popTopN = function(metaphor)
    {
        metaphor = metaphor.replace(/#/g, ' ');
        $("#topn_content").empty();
        $.get("main", {"type": "sim", "metaphor": metaphor, "lang": DataItem.lang}, function(data, status) {
              if(status == "success") {
                DataItem.topNList = data;
                //          alert(topNObj.length);
                for(var i = 0; i < DataItem.topNList.length; i++) {
                    var metaphor_sentence = "<div style=\"cursor:pointer\" class=\"cell long_cell metaphor_cell\" onclick=Displayer.popDetail(0," + i + ")>" + DataItem.topNList[i].raw_metaphor + "</div>";
                   // var valence_score = "<div class=\"cell small_cell\">" + topNObj[i].valence + "</div>";
                    var similarity_score = "<div class=\"cell small_cell\">" + DataItem.topNList[i].score.toPrecision(3) + "</div>";
                    var senti_value = "<div class=\"cell small_cell\">" + Displayer._getSentiVal(DataItem.topNList[i]) + "</div>"
                    $("#topn_content").append("<div>" + metaphor_sentence + similarity_score + senti_value + "</div>");
                    //var domains = "<div id=\"domains" + i + "\" style=\"clear:left;visibility:hidden\">";
                    /*for(var d in topNObj[i].domains) {
                        domains += "<div class=\"cell small_cell\" style=\"cursor:pointer\" onclick=domainStat(\"" + d + "\")>" + d + "</div>";
                    }
                     domains += "<div class=\"empty\"></div></div>";
                     $("#topn").append(domains);*/
                }
              }
              else {
                alert("Server Error for returning results: " + status);
              }
        });
    }
    
    this.randomQuery = function(sentence)
    {
        $.get("query", {"q": sentence, "lang": DataItem.lang}, function(data, status) {
              if(status == "success") {
                Displayer.clean_page(3);
                var metaDetail = data[0]; //would be change in the future
                DataItem.topNList = data[1];
                $("#line_c_1").html(metaDetail[0]);
                for(var i = 0; i < DataItem.topNList.length; i++) {
              
                    $("#topn").append("<div><div style=\"cursor:pointer\" onclick=Displayer.popDetail(0,"
                                      + i + ") class=\"cell long_cell metaphor_cell\">" + DataItem.topNList[i].raw_metaphor + "</div>"
                                      + "<div class=\"cell small_cell\">" + DataItem.topNList[i].score + "</div>"
                                      + "<div class=\"cell small_cell\">" + Displayer._getSentiVal(DataItem.topNList[i]) + "</div></div>");
                }
              }
              else {
                alert(status);
              }
        });
    }
    
    this._getSentiVal = function(item)
    {
        return "<span>H:&nbsp;" + item.valence + "&nbsp;</span>"
                + "<span>P:&nbsp;" + item.polarity + "&nbsp;</span>"
                + "<span>I:&nbsp;" + item.intensity + "&nbsp;</span>";
    }
};
