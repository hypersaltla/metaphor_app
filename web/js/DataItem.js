
/* the data is public to all other Objects for simplicity */

var DataItem = new function() { // Singleton pattern because we need only one object
    
    this.itemList = []; //the list of items that stores full information of each metaphor
    this.sourceList = []; // the list of source words
    this.sourceToTarget = {}; // the dictionary of source to targets mapping
    this.idList = []; // a list of metaphor IDs used by following requests
    this.totalSize = 0; //total size of data
    this.method = "source"; // default to display source list; another option is "metaphor"
    this.lang = "EN"; // default language of data items is English
    this.topNList = [];
    
    
    /*
    //accessors and setters
    this.setOffset = function(newOffset) {
        this._offset = newOffset;
    }
    
    this.getOffset = function(){
        return this._offset;
    }
    
    this.getTotalSize = function() {
        return this._totalSize;
    }
    
    this.getlistSize = function() {
        return _itemList.length;
    }
    
    this.setDefaultSize = function(defaultSize) {
        this._listSize = defaultSize;
    }
    
    this.getItemList = function() {
        return this._itemList;
    }
    
    //the code below are application specific
    this.getSourceList = function() {
        
    }
    */
};
