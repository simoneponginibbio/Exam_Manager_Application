/**
 * Implicit submission for forms - ENTER key submission
 */
(function (){
    var forms = document.getElementsByTagName("form");
    Array.from(forms).forEach(form => {
        var input_fields = form.querySelectorAll('input:not([type="button"]):not([type="hidden"])');
        var button = form.querySelector('input[type="button"]');
        Array.from(input_fields).forEach(input => {
            input.addEventListener("keydown", (e) => {
                if(e.keyCode == 13) {
                    e.preventDefault();
                    let click = new Event("click");
                    button.dispatchEvent(click);
                }
            });
        });
    });
})();

/**
 * AJAX call management
 * -----------------------------------------------------------------------------------------------------------
 * Description: Make a call to server using AJAX, and report the results.
 */
function makeCall(method, relativeUrl, form, done_callback, reset = true) {
	
    var req = new XMLHttpRequest(); //Create new request
    //Init request
	
	req.onreadystatechange = function() {
	
        switch(req.readyState){
	
            case XMLHttpRequest.UNSENT:
                
            case XMLHttpRequest.OPENED:
                
            case XMLHttpRequest.HEADERS_RECEIVED:
            
            case XMLHttpRequest.LOADING:
            	break;
                
            case XMLHttpRequest.DONE:
                
                if (checkRedirect(relativeUrl, req.responseURL)) { //Redirect if needed
                
                    done_callback(req);
                }
                break;
        }
    };

    // Opens a request
    req.open(method, relativeUrl, true);
    // Sends request
    if (form == null) {
		// Sends empty if no form provided
        req.send();
    } else {
		// Sends serialized form
        req.send(new FormData(form)); 
    }
    // Eventually resets form (if provided)
    if (form !== null && reset === true) {
		// Does not touch hidden fields, and restores default values if any
        form.reset(); 
    }
}

/**
 * Redirect check
 * -----------------------------------------------------------------------------------------------------------
 * Description: Checks if an AJAX call has been redirected. 
 *              This means that auth is no longer valid.
 */
function checkRedirect(requestURL, responseURL){
    if (responseURL) {
        let actualRequestURL = relPathToAbs(requestURL);
        // If the Url changed
        if (actualRequestURL != responseURL){ 
        	// Navigates to the url
            window.location.assign(responseURL); 
            return false;
        }
        // Passes the request to callback
        return true; 
    }
    // Else is CORS blocked or redirection loop 
    console.error("Invalid AJAX call");
    return false;
}

/**
 * Relative/Absolute path adapter
 */
function relPathToAbs(relative) {
    var stack = window.location.href.split("/");
    var parts = relative.split("/");
    // Removes current file name (or empty string)
    stack.pop();
    for (var i=0; i<parts.length; i++) {
        if (parts[i] == ".") {
            continue;
        }
        if (parts[i] == "..") {
			// Moves one directory back (or up)
            stack.pop(); 
        } else {
			// Adds it to path
            stack.push(parts[i]); 
        }
    }
    return stack.join("/"); 
}
