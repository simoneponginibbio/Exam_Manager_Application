(function() {
	
  var asc = true;

  window.addEventListener("load", () => {
	  
      var elements = document.getElementsByClassName("sortable");

      for (let i = elements.length - 1; i >= 0; i--) {
        elements[i].addEventListener("click", 
          function(){
            sortTable(elements[i].id)
          });
      }
    }, false);

  function getCellValue(tr, idx) {
    return tr.children[idx].textContent;
  }
  
  /*
  * Creates a function that compares two rows based on the cell in the idx position.
  */
  function createComparer(idx, asc) {
    return function(rowa, rowb) {
		
      // get values to compare at column idx if order is ascending, compare 1st row to 2nd , otherwise 2nd to 1st
      var v1 = getCellValue(asc ? rowa : rowb, idx),
      v2 = getCellValue(asc ? rowb : rowa, idx);
      
      // If non numeric value
      if (v1 === '' || v2 === '' || isNaN(v1) || isNaN(v2)) {
        return v1.toString().localeCompare(v2); // lexical comparison
      }

      // If numeric value
      return v1 - v2; // v1 greater than v2 --> true
    };
  }

  // For all table headers f class sortable
  function sortTable(clicked_id) {
    
    var th = document.getElementById(clicked_id);
    var table = th.closest('table');
    var rowHeaders = table.querySelectorAll('th');
    var columnIdx =  Array.from(rowHeaders).indexOf(th);
    
    var rowsArray = Array.from(table.querySelectorAll('tbody > tr'));
    
    // sort rows with the comparator function passing
    // index of column to compare, sort criterion asc or desc)
    rowsArray.sort(createComparer(columnIdx, asc));
    
    // Toggle the criterion
    asc =  !asc;
    
    // Append the sorted rows in the table body
    for (var i = 0; i < rowsArray.length; i++) {
      table.querySelector('tbody').appendChild(rowsArray[i]);
    }
  }
  
})();
