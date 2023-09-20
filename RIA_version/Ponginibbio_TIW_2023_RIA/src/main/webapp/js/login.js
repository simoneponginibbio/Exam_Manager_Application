(function() {
	
    var title = document.getElementById("title");
    var open_login_button = document.getElementById("open_login_student_button");
    var open_login_button1 = document.getElementById("open_login_button");
    var login_div = document.getElementById("login-div");
    var login_button = document.getElementById("login_button");
    var login_email_input = login_button.closest("form").querySelector('input[name="email"]');
    var login_password_input = login_button.closest("form").querySelector('input[name="password"]');
    var login_warning_div = document.getElementById('login_warning_id');
    var open_student_register_button = document.getElementById("open_student_register_login_button");
    var open_student_register_button1 = document.getElementById("open_student_register_button");
    var student_register_div = document.getElementById("student-register-div");
    var student_register_button = document.getElementById("student_register_button");
    var student_register_name_input = student_register_button.closest("form").querySelector('input[name="name"]');
    var student_register_surname_input = student_register_button.closest("form").querySelector('input[name="surname"]');
    var student_register_email_input = student_register_button.closest("form").querySelector('input[name="email"]');
    var student_register_degree_course_input = student_register_button.closest("form").querySelector('input[name="degree_course"]');
    var student_register_password_input = student_register_button.closest("form").querySelector('input[name="password"]');
    var student_register_repeat_password_input = student_register_button.closest("form").querySelector('input[name="repeat_pwd"]');
    var student_register_warning_div = document.getElementById('student_register_warning_id');
    var open_professor_register_button = document.getElementById("open_professor_register_button");
    var professor_register_div = document.getElementById("professor-register-div");
    var professor_register_button = document.getElementById("professor_register_button");
    var professor_register_name_input = professor_register_button.closest("form").querySelector('input[name="name"]');
    var professor_register_surname_input = professor_register_button.closest("form").querySelector('input[name="surname"]');
    var professor_register_email_input = professor_register_button.closest("form").querySelector('input[name="email"]');
    var professor_register_password_input = professor_register_button.closest("form").querySelector('input[name="password"]');
    var professor_register_repeat_password_input = professor_register_button.closest("form").querySelector('input[name="repeat_pwd"]');
    var professor_register_warning_div = document.getElementById('professor_register_warning_id');
	
    // Attaches to login button
    login_button.addEventListener("click", (e) => {
        var form = e.target.closest("form"); 
        login_warning_div.style.display = 'none';
        // Does a form check
        if (form.checkValidity()) {
			// Checks if the login input fields are null
			if (login_email_input.value == "" || login_password_input.value == "") {
				login_warning_div.textContent = "One or more parameters are missing";
				login_warning_div.style.display = 'block'; 
				return;
			}
            sendToServer(form, login_warning_div, 'Login', true);
        } else { 
            //If not valid, notifies
            form.reportValidity(); 
        }
    });
    
    //Attaches to login view button
	open_login_button.addEventListener("click", () => {
		login_div.style.display = 'block';
		title.style.display = 'block';
		student_register_div.style.display = 'none';
		professor_register_div.style.display = 'none';
	});
	
	//Attaches to login view button
	open_login_button1.addEventListener("click", () => {
		login_div.style.display = 'block';
		title.style.display = 'block';
		student_register_div.style.display = 'none';
		professor_register_div.style.display = 'none';
	});

    //Attaches to student register button
    student_register_button.addEventListener("click", (e) => {
        var form = e.target.closest("form"); 
        student_register_warning_div.style.display = 'none';
		const emailRegEx = new RegExp("^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$");
        // Does a form check
        if (form.checkValidity()) { 
            // Checks if the register input fields are null
            if (student_register_name_input.value == "" || student_register_surname_input.value == "" || student_register_email_input.value == "" || 
                student_register_degree_course_input.value == "" || student_register_password_input.value == "" || student_register_repeat_password_input.value == "") {
				student_register_warning_div.textContent = "One or more parameters are missing";
				student_register_warning_div.style.display = 'block'; 
				return;
			}
            // Checks if repeat_pwd and password field are not equal. If so sets a warning
            if (student_register_repeat_password_input.value != student_register_password_input.value) {
                student_register_warning_div.textContent = "Passwords do not match";
                student_register_warning_div.style.display = 'block';
                return;
            }
            // Checks if the email is not valid. If so sets a warning
            if (!emailRegEx.test(student_register_email_input.value)) {
				student_register_warning_div.textContent = "The email is not valid";
                student_register_warning_div.style.display = 'block';
                return;
			}
            sendToServer(form, student_register_warning_div, 'RegisterStudent', false);
        } else {
	 		//If not valid, notifies
            form.reportValidity();
        }
    });
    
    //Attaches to student register view button
	open_student_register_button.addEventListener("click", () => {
		student_register_div.style.display = 'block';
		professor_register_div.style.display = 'none';
		login_div.style.display = 'none';
		title.style.display = 'none';
    });
    
    //Attaches to student register view button
	open_student_register_button1.addEventListener("click", () => {
		student_register_div.style.display = 'block';
		professor_register_div.style.display = 'none';
		login_div.style.display = 'none';
		title.style.display = 'none';
    });
    
    //Attaches to professor register button
    professor_register_button.addEventListener("click", (e) => {
        var form = e.target.closest("form"); 
        professor_register_warning_div.style.display = 'none';
		const emailRegEx = new RegExp("^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$");
        // Does a form check
        if (form.checkValidity()) { 
            // Checks if the register input fields are null
            if (professor_register_name_input.value == "" || professor_register_surname_input.value == "" || professor_register_email_input.value == "" ||
             	professor_register_password_input.value == "" || professor_register_repeat_password_input.value == "") {
				professor_register_warning_div.textContent = "One or more parameters are missing";
				professor_register_warning_div.style.display = 'block'; 
				return;
			}
            // Checks if repeat_pwd and password field are not equal. If so sets a warning
            if (professor_register_repeat_password_input.value != professor_register_password_input.value) {
                professor_register_warning_div.textContent = "Passwords do not match";
                professor_register_warning_div.style.display = 'block';
                return;
            }
            // Checks if the email is not valid. If so sets a warning
            if (!emailRegEx.test(professor_register_email_input.value)) {
				professor_register_warning_div.textContent = "The email is not valid";
                professor_register_warning_div.style.display = 'block';
                return;
			}
            sendToServer(form, professor_register_warning_div, 'RegisterProfessor', false);
        } else {
	 		//If not valid, notifies
            form.reportValidity();
        }
    });
    
    //Attaches to professor register view button
    open_professor_register_button.addEventListener("click", () => {
		professor_register_div.style.display = 'block';
		student_register_div.style.display = 'none';
		login_div.style.display = 'none';
		title.style.display = 'none';
    });

	var self = this;

    function sendToServer(form, error_div, request_url, isLogin) {
        makeCall("POST", request_url, form, (req) => {
			// Gets status code
            switch(req.status) {
                case 200:
                    if (isLogin) {
						var data = JSON.parse(req.responseText);
                    	sessionStorage.setItem('id', data.id);
                    	sessionStorage.setItem('name', data.name);
                    	sessionStorage.setItem('surname', data.surname);
                    	sessionStorage.setItem('userType', data.userType);
                    	if (data.userType === "Student") {
                    		window.location.href = "studentHome.html";
                    	}
                    	if (data.userType === "Professor") {
                    		window.location.href = "professorHome.html";
                    	}
                    } else {
						var click = new Event("click");
                        self.open_login_button.dispatchEvent(click);
					}
                    break;
                case 400: // bad request
                case 401: // unauthorized
                case 500: // server error
                    error_div.textContent = req.responseText;
                    error_div.style.display = 'block';
                    break;
                default:
                    error_div.textContent = "Request reported status " + req.status;
                    error_div.style.display = 'block';
            }
        });
    }
})();
