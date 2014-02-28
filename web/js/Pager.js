/* A Page controller interacts with user and DataItem Object */

var Pager = new function() { // Singleton pattern because we need only one object
    
    this._perPage = 20; //default page size
    this._offset = 0; //offset of the current page
    this._N = 0; // the number of pages;
    this.areaID = ""; // the ID of the HTML tag in which the page should be displayed;
    this.cssClass = ""; // the CSS class of each line of data, used for CSS;
   // this.cssID = ""; // the ID of each line of data, used for style;
  //  this.js_event = ""; // the event caused on each line of data;
  //  this.listener = ""; // the event listner of each line of data, used for javascript actions;
    
    this.setView = function(area_id, css_class) {
        this.areaID = area_id;
        this.cssClass = css_class;
    };
    
    this.showPage = function(pageN) { //parameter is the page number which starts from 1
        pageN = Number(pageN) - 1;
        if(pageN < 0 || (pageN >= this._N && pageN > 0)) { //return when the pageN is out of range but exclude the initial case (when pageN == 0)
            return;
        }
        this._offset = Number(pageN) * this._perPage;
        this._getRangeOfData();
    };
    
    this._getRangeOfData = function() {
        if(DataItem.method == "metaphor") {
            $.get("main", {"type": "fetch", "method": "metaphor", "start": this._offset, "end": this._perPage + this._offset, "lang": DataItem.lang}, function(data, status) {
                  if(status == "success") {
                  /* reset the data in the DataItem Object */
                  DataItem.itemList = data.items;
                  DataItem.totalSize = data.total_size;
                  DataItem.idList = data.id_list;
                  Pager._N = Math.ceil(DataItem.totalSize / Pager._perPage);
    
                  /* display the data in the page */
                  Pager._pageDisp();
                  }
                  else {
                  alert("Server Error Status:" + status);
                  }
                  });
        }
        else if(DataItem.method == "source"){
            $.get("main", {"type": "fetch", "method": "source", "start": this._offset, "end": this._perPage + this._offset, "lang": DataItem.lang}, function(data, status) {
                  if(status == "success") {
                  /* reset the data in the DataItem Object */
                  DataItem.totalSize = data.total_size;
                  DataItem.sourceList = data.source_list;
                  DataItem.sourceToTarget = data.st_mappings;
                  Pager._N = Math.ceil(DataItem.totalSize / Pager._perPage);
                  /* display the data in the page */
                  Pager._pageDisp();
                  } else {
                  alert("Server Error status:" + status);
                  }
                  });
        }
    };

    this._pageDisp = function() {
        
        if(!this._N) {
            return;
        }
 //       alert(this._N);
        $(this.areaID).empty();
        var displayN = 0;
        var cur_data = [];
        //alert(DataItem.method);
        if(DataItem.method == "source") {
            displayN = DataItem.sourceList.length;
            cur_data = DataItem.sourceList;
            $("#source").html("<div class=\"small_cell title_cell\">source</div>");
        }
        else {
            displayN = DataItem.itemList.length;
            for(var i = 0; i < displayN; i++) {
                cur_data.push(DataItem.itemList[i].raw_metaphor);
            }
        }
        var pageN = this._offset / this._perPage + 1;
        
        for(var i = this._offset; i < this._offset + displayN; i++) {
            //console.log(cur_data[i - this._offset]);
            $(this.areaID).append("<div onclick=" + this._onclickItemMethod(DataItem.method, i - this._offset) + " class=\"" + this.cssClass + "\">" + (i + 1) + ".  <span class='clickable'>" + cur_data[i - this._offset] + "</span></div>");
        }
        $(this.areaID).append("<div style=\"color:#585858;margin-top:3px;\">"
                + "<span style=\"cursor:pointer\" onclick=Pager.showPage(" + (pageN - 1) + ")>&lt;&lt;</span>"
                + "<span>&nbsp;&nbsp;" + (pageN) + "&nbsp;&nbsp;</span>"
                + "<span style=\"cursor:pointer\" onclick=Pager.showPage(" + (pageN + 1) + ")>&gt;&gt;</span>"
                + "<span>&nbsp;&nbsp;Totally&nbsp;<span style=\"color:red\">" + this._N + "</span>&nbsp;pages</span></div>");
    }
    
    this._onclickItemMethod = function(method, index) {
        index = Number(index);
        if(method == "source") {
            return "Displayer.popTarget(\'" + DataItem.sourceList[index].replace(/\s/g, "#") + "\')";
        }
        else {
            return "Displayer.popDetail(1," + index + ")";
        }
    };
    
    //this.test = 10;
    
};
