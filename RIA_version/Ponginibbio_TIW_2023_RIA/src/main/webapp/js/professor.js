(function() {
	
    var user, homeTitle, courseList, dateList, registrationList, modifyGrade, report, multipleInsertGrade, warning;
    
    var pageManager = new PageManager();
    
    window.addEventListener("load", () => {
        pageManager.start();
        pageManager.refresh();
    });

    function PageManager(){
        
        this.start = function(){
            user = new User(
				sessionStorage.getItem('id'),
				sessionStorage.getItem('name'),
				sessionStorage.getItem('surname'),
				document.getElementById("name"),
				document.getElementById("surname"),
				document.getElementById("headerId"),
				document.getElementById("headerName"),
				document.getElementById("headerSurname"),
				document.getElementById("home-button"),
                document.getElementById("logout-button")
            );
            homeTitle = document.getElementById("home-title");
            courseList = new CourseList(
				document.getElementById("courses-div"),
				document.getElementById("courses-title"),
				document.getElementById("courses")
			);
			dateList = new DateList(
				document.getElementById("dates-div"),
				document.getElementById("dates-title"),
				document.getElementById("dates")
			);
			registrationList = new RegistrationList(
				document.getElementById("registration-div"),
				document.getElementById("registration-title"),
				document.getElementById("registration")
			);
			modifyGrade = new ModifyGrade(
				document.getElementById("modify-grade-div"),
				document.getElementById("modify-grade-title"),
				document.getElementById("modify-grade")
			);
			report = new Report(
				document.getElementById("report-div"),
				document.getElementById("report-title"),
				document.getElementById("report")
			);
			multipleInsertGrade = new MultipleInsertGrade(
				document.getElementById("multiple-insert-grade-div"),
				document.getElementById("multiple-insert-grade-title"),
				document.getElementById("multiple-insert-grade")
			);
			warning = document.getElementById("warning");
        };
        
        this.refresh = () => {
            user.show();
            courseList.show();
        };
        
        this.hide = () => {
			courseList.div.style.display = "none";
			courseList.title.style.display = "none";
			courseList.courses.style.display = "none";
			
			dateList.div.style.display = "none";
			dateList.title.style.display = "none";
			dateList.dates.style.display = "none";
			
			registrationList.div.style.display = "none";
			registrationList.title.style.display = "none";
			registrationList.registration.style.display = "none";
			
			modifyGrade.div.style.display = "none";
			modifyGrade.title.style.display = "none";
			modifyGrade.modify_grade.style.display = "none";
			
			report.div.style.display = "none";
			report.title.style.display = "none";
			report.report.style.display = "none";
			
			multipleInsertGrade.div.style.display = "none";
			multipleInsertGrade.title.style.display = "none";
			multipleInsertGrade.multiple_insert_grade.style.display = "none";
			
			warning.style.display = "none";
		};
        
    }
    
    function User(
        _id, 
        _name, 
        _surname,
        homeName,
        homeSurname,
        headerId,
        headerName,
        headerSurname,
        _home_button,
        _logout_button) {
			
		this.id = _id;
        this.name = _name;
        this.surname = _surname;
        this.home_button = _home_button;
        this.logout_button = _logout_button;
        
        this.home_button.addEventListener("click", () => {
			pageManager.hide();
            this.show();
        });
        
        this.logout_button.addEventListener("click", () => {
            sessionStorage.clear();
        });

        this.show = function(){
	        homeName.textContent = this.name;
            homeSurname.textContent = this.surname;
            headerId.textContent = this.id;
            headerName.textContent = this.name;
            headerSurname.textContent = this.surname;
        }
    }
    
    function CourseList(
		_div,
		_title,
        _courses) {

		this.div = _div;
		this.title = _title;
        this.courses = _courses;

        var self = this; 

        this.show = function() {
            // Requests and updates with the results
            makeCall("GET", 'GoToHome', null, (req) => {
                switch(req.status) {
                    case 200:
                        var courses = JSON.parse(req.responseText);
                        pageManager.hide();
                        self.update(courses);
                        break;
                    case 400: // bad request
                    case 401: // unauthorized
                    case 500: // server error
                    	warning.textContent = req.responseText;
                    	warning.style.display = "block";
                        break;
                    default: //Error
                    	warning.textContent = "Request reported status " + req.status;
                    	warning.style.display = "block";
                        break;
                }
            });
        };
        
        this.update = function(_courses) {
			self.div.style.display = "none";
			self.title.style.display = "none";
            self.title.innerHTML = "";
            self.courses.style.display = "none";
            self.courses.innerHTML = "";
            homeTitle.style.display = "none";
            homeTitle.innerHTML = "";
			let card, card_title, card_data, b, open_button;
			let i = 0;
			
			_courses.forEach((course) => {
				card = document.createElement("div");
				card.className = "card card-blue";
				
				if (i % 2 === 1) {
					card.className += " even";
				}
				
				card_title = document.createElement("div");
				card_title.className = "card-title";
				card_title.textContent = "Course Name: ";
				card_title.appendChild(document.createTextNode(course[0].name));
				card.appendChild(card_title);

				card_data = document.createElement("div");
				card_data.className = "card-data";
				
				b = document.createElement("b");
				b.textContent = "Number of student enrolled: ";
				card_data.appendChild(b);
				card_data.appendChild(document.createTextNode(course[1]));

				card.appendChild(card_data);
				
				open_button = document.createElement("a");
				open_button.className = "btn btn-gossamer btn-small btn-primary";
				open_button.textContent = "Open";
				
				// Sets the event on click on the button
				open_button.addEventListener("click", () => {
					dateList.show(course[0].name);
					self.div.style.display = "none";
					self.courses.style.display = "none";
					self.title.style.display = "none";
				});
				
				card.appendChild(open_button);
				
				self.courses.appendChild(card);
				
				i++;
			});
			homeTitle.innerHTML = "Welcome to your home,";
			homeTitle.style.display = "block";
			self.title.innerHTML = "Here there are your courses:";
			self.title.style.display = "block";
			self.courses.style.display = "block";
			self.div.style.display = "block";
        };
    }
    
    function DateList(
		_div,
		_title,
        _dates) {

		this.div = _div;
		this.title = _title;
        this.dates = _dates;

        var self = this; 

        this.show = function(name) {
            // Requests and updates with the results
            makeCall("GET", 'GoToExamDates?courseName=' + name, null, (req) => {
                switch(req.status) {
                    case 200:
                        var data = JSON.parse(req.responseText);
                        pageManager.hide();
                        self.update(data.courseName, data.examDatesAndCount);
                        break;
                    case 400: // bad request
                    case 401: // unauthorized
                    case 500: // server error
                    	warning.textContent = req.responseText;
                    	warning.style.display = "block";
                        break;
                    default: //Error
                    	warning.textContent = "Request reported status " + req.status;
                    	warning.style.display = "block";
                        break;
                }
            });
        };
        
        this.update = function(_name, _dates) {
			self.div.style.display = "none";
			self.title.style.display = "none";
            self.title.innerHTML = "";
            self.dates.style.display = "none";
            self.dates.innerHTML = "";
            homeTitle.style.display = "none";
            homeTitle.innerHTML = "";
			let card, card_title, open_button;
			let i = 0;
			
			_dates.forEach((date) => {
				card = document.createElement("div");
				card.className = "card card-blue";
				
				if (i % 2 === 1) {
					card.className += " even";
				}
				
				card_title = document.createElement("div");
				card_title.className = "card-title";
				card_title.textContent = "Exam Date: ";
				card_title.appendChild(document.createTextNode(date[0]));
				card.appendChild(card_title);
				
				card_data = document.createElement("div");
				card_data.className = "card-data";
				card_data.textContent = "Number of student registered: ";
				card_data.appendChild(document.createTextNode(date[1]));
				card.appendChild(card_data);
				
				open_button = document.createElement("a");
				open_button.className = "btn btn-gossamer btn-small btn-primary";
				open_button.textContent = "Open";
				
				// Sets the event on click on the button
				open_button.addEventListener("click", () => {
					self.title.style.display = "none";
					self.dates.style.display = "none";
					registrationList.show(_name, date[0]);
				});
				
				card.appendChild(open_button);
				
				self.dates.appendChild(card);
				
				i++;
			});
			homeTitle.innerHTML = "Welcome to the exam dates page,";
			homeTitle.style.display = "block";
			self.title.innerHTML = "Here there are your exam dates for the course " + _name + ":";
			self.title.style.display = "block";
			self.dates.style.display = "block";
			self.div.style.display = "block";
        };
    }
    
    function RegistrationList(
		_div,
		_title,
		_registration) {
		
		this.div = _div;
		this.title = _title;
		this.registration = _registration;

        var self = this;

        this.show = function(name, date) {
            // Requests and updates with the results
            makeCall("GET", 'GoToExamRegistrations?courseName=' + name + '&examDate=' + date, null, (req) => {
                switch(req.status) {
                    case 200:
                        var data = JSON.parse(req.responseText);
                        pageManager.hide();
                        self.update(data);
                        break;
                    case 400: // bad request
                    case 401: // unauthorized
                    case 500: // server error
                    	warning.textContent = req.responseText;
                    	warning.style.display = "block";
                        break;
                    default: //Error
                    	warning.textContent = "Request reported status " + req.status;
                    	warning.style.display = "block";
                        break;
                }
            });
        };
        
        this.update = function(_data) {
			self.div.style.display = "none";
			self.title.style.display = "none";
            self.title.innerHTML = "";
            self.registration.style.display = "none";
            self.registration.innerHTML = "";
            homeTitle.style.display = "none";
            homeTitle.innerHTML = "";
            
			let table, thead, tbody, tr, th, a, b, card_title, modify_button, publish_button, verbalise_button;
			
			table = document.createElement("table");
			thead = document.createElement("thead");
			tr = document.createElement("tr");
			
			th = document.createElement("th");
			th.id = "id";
			th.className = "sortable";
			card_title = document.createElement("div");
			card_title.className = "card-title";
			a = document.createElement("a");
			b = document.createElement("b");
			b.textContent = "Id";
			a.appendChild(b);
			a.onclick();
			card_title.appendChild(a);
			th.appendChild(card_title);
			tr.appendChild(th);
			
			th = document.createElement("th");
			th.id = "surname";
			th.className = "sortable";
			card_title = document.createElement("div");
			card_title.className = "card-title";
			a = document.createElement("a");
			b = document.createElement("b");
			b.textContent = "Surname and Name";
			a.appendChild(b);
			card_title.appendChild(a);
			th.appendChild(card_title);
			tr.appendChild(th);
			
			th = document.createElement("th");
			th.id = "email";
			th.className = "sortable";
			card_title = document.createElement("div");
			card_title.className = "card-title";
			a = document.createElement("a");
			b = document.createElement("b");
			b.textContent = "Email";
			a.appendChild(b);
			card_title.appendChild(a);
			th.appendChild(card_title);
			tr.appendChild(th);
			
			th = document.createElement("th");
			th.id = "degree_course";
			th.className = "sortable";
			card_title = document.createElement("div");
			card_title.className = "card-title";
			a = document.createElement("a");
			b = document.createElement("b");
			b.textContent = "Degree Course";
			a.appendChild(b);
			card_title.appendChild(a);
			th.appendChild(card_title);
			tr.appendChild(th);
			
			th = document.createElement("th");
			th.id = "grade";
			th.className = "sortable";
			card_title = document.createElement("div");
			card_title.className = "card-title";
			a = document.createElement("a");
			b = document.createElement("b");
			b.textContent = "Grade";
			a.appendChild(a);
			card_title.appendChild(b);
			th.appendChild(card_title);
			tr.appendChild(th);
			
			th = document.createElement("th");
			th.id = "judgment";
			th.className = "sortable";
			card_title = document.createElement("div");
			card_title.className = "card-title";
			a = document.createElement("a");
			b = document.createElement("b");
			b.textContent = "Judgment";
			a.appendChild(b);
			card_title.appendChild(a);
			th.appendChild(card_title);
			tr.appendChild(th);
			
			thead.appendChild(tr);
			table.appendChild(thead);
			
			tbody = document.createElement("tbody");
			
			let i = 0;
			_data.forEach((registration) => {
				tr = document.createElement("tr");
				tr.className = "card card-blue";
				
				if (i % 2 === 1) {
					tr.className += " even";
				}
				
				th = document.createElement("th");
				card_title = document.createElement("div");
				card_title.className = "card-title";
				card_title.textContent = registration.student.id;
				th.appendChild(card_title);
				tr.appendChild(th);
				
				th = document.createElement("th");
				card_title = document.createElement("div");
				card_title.className = "card-title";
				card_title.textContent = registration.student.surname + " " + registration.student.name;
				th.appendChild(card_title);
				tr.appendChild(th);
				
				th = document.createElement("th");
				card_title = document.createElement("div");
				card_title.className = "card-title";
				card_title.textContent = registration.student.email;
				th.appendChild(card_title);
				tr.appendChild(th);
				
				th = document.createElement("th");
				card_title = document.createElement("div");
				card_title.className = "card-title";
				card_title.textContent = registration.student.degree_course;
				th.appendChild(card_title);
				tr.appendChild(th);
				
				th = document.createElement("th");
				card_title = document.createElement("div");
				card_title.className = "card-title";
				card_title.textContent = registration.grade;
				th.appendChild(card_title);
				tr.appendChild(th);
				
				th = document.createElement("th");
				card_title = document.createElement("div");
				card_title.className = "card-title";
				card_title.textContent = registration.judgment;
				th.appendChild(card_title);
				tr.appendChild(th);
				
				if (registration.judgment !== "verbalised" && registration.judgment !== "declined" && registration.judgment !== "published") {
					modify_button = document.createElement("a");
					modify_button.className = "btn btn-gossamer btn-small btn-primary";
					modify_button.textContent = "Modify";
					
					modify_button.addEventListener("click", () =>{
						
			            modifyGrade.show(registration.student.id, _data[0].exam.course.name, _data[0].exam.date);
			        });
					
					tr.appendChild(modify_button);
				}
				
				tbody.appendChild(tr);
				i++;
			});
			
			table.appendChild(tbody);
			
			self.registration.appendChild(table);
			
			if (self.isPublishable(_data)) {
				publish_button = document.createElement("a");
				publish_button.className = "btn btn-gossamer btn-medium btn-primary register";
				publish_button.textContent = "Publish Grades";
				
				publish_button.addEventListener("click", () =>{
						// Requests and updates with the results
			            makeCall("POST", 'GoToPublishGrades?courseName=' + _data[0].exam.course.name + '&examDate=' + _data[0].exam.date, null, (req) => {
			                switch(req.status) {
			                    case 200:
									pageManager.hide();
			                        self.show(_data[0].exam.course.name, _data[0].exam.date);
			                        break;
			                    case 400: // bad request
			                    case 401: // unauthorized
			                    case 500: // server error
			                    	warning.textContent = req.responseText;
                    				warning.style.display = "block";
			                        break;
			                    default: //Error
			                    	warning.textContent = "Request reported status " + req.status;
                    				warning.style.display = "block";
			                        break;
			                }
			            });
			        });
					
				self.registration.appendChild(publish_button);
			}
			
			if (self.isVerbalisable(_data)) {
				verbalise_button = document.createElement("a");
				verbalise_button.className = "btn btn-gossamer btn-medium btn-primary register";
				verbalise_button.textContent = "Verbalise Grades";
				
				verbalise_button.addEventListener("click", () =>{
						
			            report.show(_data[0].exam.course.name, _data[0].exam.date);
			        });
					
				self.registration.appendChild(verbalise_button);
			}
			
			if (self.isMultiInsertable(_data)) {
				insert_button = document.createElement("a");
				insert_button.className = "btn btn-gossamer btn-medium btn-primary register";
				insert_button.textContent = "Insert Multiple Grades";
				
				insert_button.addEventListener("click", () =>{
			          
			            multipleInsertGrade.show(_data[0].exam.course.name, _data[0].exam.date);
			            
			        });
					
				self.registration.appendChild(insert_button);
			}
				
			homeTitle.innerHTML = "Welcome to the exam registrations page,";
			homeTitle.style.display = "block";
			self.title.innerHTML = "Here there are the students registered for the exam " + _data[0].exam.course.name + " of " + _data[0].exam.date + " :";
			self.title.style.display = "block";
			self.registration.style.display = "block";
			self.div.style.display = "block";
        };
        
        this.isPublishable = function(_registrations) {
			_registrations.forEach((registration) => {
				if (registration.judgment === "insered") {
					return true;
				}
			});
			return false;
		}
		
		this.isVerbalisable = function(_registrations) {
			_registrations.forEach((registration) => {
				if (registration.judgment === "published" || registration.judgment === "declined") {
					return true;
				}
			});
			return false;
		}
		
		this.isMultiInsertable = function(_registrations) {
			_registrations.forEach((registration) => {
				if (registration.judgment === "uninsered") {
					return true;
				}
			});
			return false;
		}
        
    }
    
    function ModifyGrade(
		_div,
		_title,
        _modify_grade) {

		this.div = _div;
		this.title = _title;
        this.modify_grade = _modify_grade;

        var self = this; 

        this.show = function(id, name, date) {
			// Requests and updates with the results
            makeCall("GET", 'GoToModifyGrade?studentId=' + id + '&courseName=' + name + '&examDate=' + date, null, (req) => {
                switch(req.status) {
                    case 200:
                        var data = JSON.parse(req.responseText);
                        pageManager.hide();
                        self.update(data);
                        break;
                    case 400: // bad request
                    case 401: // unauthorized
                    case 500: // server error
                    	warning.textContent = req.responseText;
                    	warning.style.display = "block";
                        break;
                    default: //Error
                    	warning.textContent = "Request reported status " + req.status;
                    	warning.style.display = "block";
                        break;
                }
            });
		}
		
		this.update = function(_data) {
			self.div.style.display = "none";
			self.title.style.display = "none";
            self.title.innerHTML = "";
            self.modify_grade.style.display = "none";
            self.modify_grade.innerHTML = "";
            homeTitle.style.display = "none";
            homeTitle.innerHTML = "";
            
			let card, card_title, b, form,form_group, label, input, button;
			
			card = document.createElement("div");
			card.className = "card card-blue";
			
			card_title = document.createElement("div");
			card_title.className = "card-title";
			
			b = document.createElement("b");
			b.textContent = "Personal Id: " + _data.student.id;
			card_title.appendChild("b");
			card_title.appendChild(document.createElement("br"));
			
			b = document.createElement("b");
			b.textContent = "Name: " + _data.student.name;
			card_title.appendChild("b");
			card_title.appendChild(document.createElement("br"));
				
			b = document.createElement("b");
			b.textContent = "Surname: " + _data.student.surname;
			card_title.appendChild("b");
			card_title.appendChild(document.createElement("br"));
				
			b = document.createElement("b");
			b.textContent = "Email: " + _data.student.email;
			card_title.appendChild("b");
			card_title.appendChild(document.createElement("br"));
				
			b = document.createElement("b");
			b.textContent = "Degree course: " + _data.student.degree_course;
			card_title.appendChild("b");
			card_title.appendChild(document.createElement("br"));
				
			b = document.createElement("b");
			b.textContent = "Course Name: " + _data.exam.course.name;
			card_title.appendChild("b");
			card_title.appendChild(document.createElement("br"));
				
			b = document.createElement("b");
			b.textContent = "Exam Date: " + _data.exam.date;
			card_title.appendChild("b");
			card_title.appendChild(document.createElement("br"));
				
			b = document.createElement("b");
			b.textContent = "Grade: " + _data.grade;
			card_title.appendChild("b");
			card_title.appendChild(document.createElement("br"));
			
			card.appendChild(card_title);
			
			form = document.createElement("form");
			form.className = "login-form";
			form.id = "new_grade";
			
			form_group = document.createElement("div");
			form_group.className = "form-group";
			form_group.style.marginTop = "20px";
			
			label = document.createElement("label");
			label.htmlFor = "text";
			label.textContent = "New Grade:";
			form_group.appendChild(label);
			
			input = document.createElement("input");
			input.type = "text";
			input.placeholder = "Enter the new grade";
			input.name = "newGrade";
			input.required = true;
			form_group.appendChild(input);
			
			form.appendChild(form_group);
			
			button = document.createElement("button");
			button.className = "btn btn-large btn-gossamer";
			button.type = "submit";
			button.textContent = "Modify Grade";
			
			button.addEventListener("click", () => {
				
				let form_new_grade = document.getElementById("new_grade");
				let form_input = form_new_grade.querySelector("input[name='newGrade']");
				
				if (self.form_new_grade.checkValidity()) {
					
					if (form_input === "" || form_input !== "absent" || form_input !== "failed" || form_input !== "tried again" || 
						form_input !== "18" || form_input !== "19" || form_input !== "20" || form_input !== "21" || 
						form_input !== "22" || form_input !== "23" || form_input !== "24" || form_input !== "25" || 
						form_input !== "26" || form_input !== "27" || form_input !== "28" || form_input !== "29" || 
						form_input !== "30" || form_input !== "30L") {
						warning.textContent = "invalid new insered grade, the grade must be absent, rejected, tried again or between 18 and 30L";
						warning.style.display = "block";
						return;
					}
				
					makeCall("POST", 'GoToInsertGrade?studentId=' + _data.student.id + '&courseName=' + _data.exam.course.name + '&examDate=' + _data.exam.date, form_new_grade, (req) => {
                    switch(req.status) {
                        case 200:
							pageManager.hide();
                        	registrationList.show(_data.exam.course.name, _data.exam.date);
                            break;
                        case 400: // bad request
                        case 401: // unauthorized
                        case 500: // server error
                        	warning.textContent = req.responseText;
                            warning.style.display = "block";
                            break;
                        default: //Error
                        	warning.textContent = "Request reported status " + req.status;
                            warning.style.display = "block";
                    	}
                	});
                	
				} else {
					form.reportValidity();
				}
			});
			
			form.appendChild(button);
			
			card.append(form);
			
			self.modify_grade.appendChild(card);
			
			homeTitle.innerHTML = "Welcome to the grade modifier page,";
			homeTitle.style.display = "block";
			self.title.innerHTML = "Here there are the student information for the exam " + _data.courseName + " of " + _data.examDate + ":";
			self.title.style.display = "block";
			self.modify_grade.style.display = "block";
			self.div.style.display = "block";
        };
		
	}
	
    function Report(
		_div,
		_title,
        _report) {

		this.div = _div;
		this.title = _title;
        this.report = _report;

        var self = this; 

        this.show = function(name, date) {
			// Requests and updates with the results
            makeCall("GET", 'GoToVerbaliseGrades?courseName=' + name + '&examDate=' + date, null, (req) => {
                switch(req.status) {
                    case 200:
                        var data = JSON.parse(req.responseText);
                        pageManager.hide();
                        self.update(data);
                        break;
                    case 400: // bad request
                    case 401: // unauthorized
                    case 500: // server error
                    	warning.textContent = req.responseText;
                    	warning.style.display = "block";
                        break;
                    default: //Error
                    	warning.textContent = "Request reported status " + req.status;
                    	warning.style.display = "block";
                        break;
                }
            });
		}
		
		this.update = function(_data) {
			self.div.style.display = "none";
			self.title.style.display = "none";
            self.title.innerHTML = "";
            self.report.style.display = "none";
            self.report.innerHTML = "";
            homeTitle.style.display = "none";
            homeTitle.innerHTML = "";
            
			let table, thead, tbody, tr, th, card_title;
			
			table = document.createElement("table");
			thead = document.createElement("thead");
			tr = document.createElement("tr");
			th = document.createElement("th");
			
			card_title = document.createElement("div");
			card_title.className = "card-title";
			card_title.textContent = "Id";
			th.appendChild(card_title);
			tr.appendChild(th);
			
			card_title = document.createElement("div");
			card_title.className = "card-title";
			card_title.textContent = "Surname and Name";
			th.appendChild(card_title);
			tr.appendChild(th);
			
			card_title = document.createElement("div");
			card_title.className = "card-title";
			card_title.textContent = "Email";
			th.appendChild(card_title);
			tr.appendChild(th);
			
			card_title = document.createElement("div");
			card_title.className = "card-title";
			card_title.textContent = "Degree Course";
			th.appendChild(card_title);
			tr.appendChild(th);
			
			card_title = document.createElement("div");
			card_title.className = "card-title";
			card_title.textContent = "Grade";
			th.appendChild(card_title);
			tr.appendChild(th);
			
			thead.appendChild(tr);
			table.appendChild(thead);
			
			tbody = document.createElement("tbody");
			
			let i = 0;
			_data.report.forEach((report) => {
				tr = document.createElement("tr");
				tr.className = "card card-blue";
				
				if (i % 2 === 1) {
					tr.className += " even";
				}
				
				th = document.createElement("th");
				card_title = document.createElement("div");
				card_title.className = "card-title";
				card_title.textContent = report[0].student.id;
				th.appendChild(card_title);
				tr.appendChild(th);
				
				th = document.createElement("th");
				card_title = document.createElement("div");
				card_title.className = "card-title";
				card_title.textContent = report[0].student.surname + " " + report[0].student.name;
				th.appendChild(card_title);
				tr.appendChild(th);
				
				th = document.createElement("th");
				card_title = document.createElement("div");
				card_title.className = "card-title";
				card_title.textContent = report[0].student.email;
				th.appendChild(card_title);
				tr.appendChild(th);
				
				th = document.createElement("th");
				card_title = document.createElement("div");
				card_title.className = "card-title";
				card_title.textContent = report[0].student.degree_course;
				th.appendChild(card_title);
				tr.appendChild(th);
				
				th = document.createElement("th");
				card_title = document.createElement("div");
				card_title.className = "card-title";
				card_title.textContent = report[1];
				th.appendChild(card_title);
				tr.appendChild(th);
				
				tbody.appendChild(tr);
				i++;
			});
			
			self.report.appendChild(table);
			
			homeTitle.innerHTML = "Welcome to the exam report page,";
			homeTitle.style.display = "block";
			self.title.setAttribute('style', 'white-space: pre;');
			self.title.innerHTML = "Here there is the report for the exam " + _data.courseName + " of " + _data.examDate + ":\r\n";
			self.title.innerHTML += "Report Id: " + _data.report[0][0].id + "     Timestamp: " + _data.timestamp;
			self.title.style.display = "block";
			self.report.style.display = "block";
			self.div.style.display = "block";
        };
		
	}
	
	function MultipleInsertGrade(
		_div,
		_title,
        _multiple_insert_grade) {

		this.div = _div;
		this.title = _title;
        this.multiple_insert_grade = _multiple_insert_grade;

        var self = this; 

        this.show = function(name, date) {
			// Requests and updates with the results
            makeCall("GET", 'GoToMultipleInsertGrade?courseName=' + name + '&examDate=' + date, null, (req) => {
				switch(req.status) {
					case 200:
						var data = JSON.parse(req.responseText);
						pageManager.hide();
						self.update(data);
						break;
					case 400: // bad request
			        case 401: // unauthorized
			        case 500: // server error
			        	warning.textContent = req.responseText;
                    	warning.style.display = "block";
			            break;
			        default: //Error
			        	warning.textContent = "Request reported status " + req.status;
                    	warning.style.display = "block";
			            break;
			    }
			});
		}
		
		this.update = function(_data) {
			self.div.style.display = "none";
			self.title.style.display = "none";
            self.title.innerHTML = "";
            self.multiple_insert_grade.style.display = "none";
            self.multiple_insert_grade.innerHTML = "";
            homeTitle.style.display = "none";
            homeTitle.innerHTML = "";
            
			let card, card_title, b, form,form_group, label, input, button;
			
			_data.forEach((registration) => {
				card = document.createElement("div");
				card.className = "card card-blue";
				
				card_title = document.createElement("div");
				card_title.className = "card-title";
				
				b = document.createElement("b");
				b.textContent = "Personal Id: " + registration.student.id;
				card_title.appendChild("b");
				card_title.appendChild(document.createElement("br"));
				
				b = document.createElement("b");
				b.textContent = "Name: " + registration.student.name;
				card_title.appendChild("b");
				card_title.appendChild(document.createElement("br"));
					
				b = document.createElement("b");
				b.textContent = "Surname: " + registration.student.surname;
				card_title.appendChild("b");
				card_title.appendChild(document.createElement("br"));
					
				b = document.createElement("b");
				b.textContent = "Email: " + registration.student.email;
				card_title.appendChild("b");
				card_title.appendChild(document.createElement("br"));
					
				b = document.createElement("b");
				b.textContent = "Degree course: " + registration.student.degree_course;
				card_title.appendChild("b");
				card_title.appendChild(document.createElement("br"));
					
				b = document.createElement("b");
				b.textContent = "Course Name: " + registration.exam.course.name;
				card_title.appendChild("b");
				card_title.appendChild(document.createElement("br"));
					
				b = document.createElement("b");
				b.textContent = "Exam Date: " + registration.exam.date;
				card_title.appendChild("b");
				card_title.appendChild(document.createElement("br"));
					
				b = document.createElement("b");
				b.textContent = "Grade: " + registration.grade;
				card_title.appendChild("b");
				card_title.appendChild(document.createElement("br"));
				
				card.appendChild(card_title);
				
				form = document.createElement("form");
				form.className = "login-form";
				
				form_group = document.createElement("div");
				form_group.className = "form-group";
				form_group.style.marginTop = "20px";
				
				label = document.createElement("label");
				label.htmlFor = "text";
				label.textContent = "New Grade:";
				form_group.appendChild(label);
				
				input = document.createElement("input");
				input.type = "text";
				input.placeholder = "Enter the new grade";
				input.name = "newGrade";
				input.required = true;
				form_group.appendChild(input);
				
				form.appendChild(form_group);
				
				card.append(form);
				
				self.multiple_insert_grade.appendChild(card);
			});
			
			button = document.createElement("button");
			button.className = "btn btn-large btn-gossamer";
			button.type = "submit";
			button.textContent = "Modify Grade";
				
			button.addEventListener("click", () => {
				
				let forms_new_grade = document.getElementsByClassName("login-form");
				
				for (let i = 0; i < forms_new_grade.length; ++i) {
					
					let form_input = forms_new_grade[i].querySelector("input[name='newGrade']");
					
					if (forms_new_grade[i].checkValidity()) {
							
						if (form_input === "" || form_input !== "absent" || form_input !== "failed" || form_input !== "tried again" || 
							form_input !== "18" || form_input !== "19" || form_input !== "20" || form_input !== "21" || 
							form_input !== "22" || form_input !== "23" || form_input !== "24" || form_input !== "25" || 
							form_input !== "26" || form_input !== "27" || form_input !== "28" || form_input !== "29" || 
							form_input !== "30" || form_input !== "30L") {
							warning.textContent = "invalid new insered grade, the grade must be absent, rejected, tried again or between 18 and 30L";
							warning.style.display = "block";
							
						} else {
						
							makeCall("POST", 'GoToInsertGrade?studentId=' + _data[i].student.id + '&courseName=' + _data[0].exam.course.name + '&examDate=' + _data[0].exam.date, forms_new_grade[i], (req) => {
			                   	switch(req.status) {
			                        case 200:
			                        	break;
			                        case 400: // bad request
			                        case 401: // unauthorized
			                   		case 500: // server error
			                   			warning.textContent = req.responseText;
                    					warning.style.display = "block";
			                        	break;
			                    	default: //Error
			                    		warning.textContent = "Request reported status " + req.status;
                    					warning.style.display = "block";
                    					break;
			                    }
			                });
		                }
		                	
					} else {
						form.reportValidity();
					}
				}
				registrationList.show(_data[0].exam.course.name, _data[0].exam.date);
			});
			
			self.multiple_insert_grade.appendChild(button);
			
			homeTitle.innerHTML = "Welcome to the multiple grade modifier page,";
			homeTitle.style.display = "block";
			self.title.innerHTML = "Here there are the students information for the exam " + _data[0].exam.course.name + " of " + _data[0].exam.date + ":";
			self.title.style.display = "block";
			self.multiple_insert_grade.style.display = "block";
			self.div.style.display = "block";
        };
		
	}
	
})();
