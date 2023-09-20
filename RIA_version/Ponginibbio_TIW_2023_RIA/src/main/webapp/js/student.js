(function() {
	
    var user, homeTitle, courseList, dateList, registration, warning;
    
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
			registration = new Registration(
				document.getElementById("registration-div"),
				document.getElementById("registration")
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
			
			registration.div.style.display = "none";
			registration.registration.style.display = "none";
			
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
				card_title.appendChild(document.createTextNode(course.name));
				card.appendChild(card_title);

				card_data = document.createElement("div");
				card_data.className = "card-data";
				
				b = document.createElement("b");
				b.textContent = "Professor: ";
				card_data.appendChild(b);
				card_data.appendChild(document.createTextNode(course.professor.name + " " + course.professor.surname));

				card.appendChild(card_data);
				
				open_button = document.createElement("a");
				open_button.className = "btn btn-gossamer btn-small btn-primary";
				open_button.textContent = "Open";
				
				// Sets the event on click on the button
				open_button.addEventListener("click", () => {
					dateList.show(course.name);
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
                        self.update(data.courseName, data.examDates);
                        
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
				card_title.appendChild(document.createTextNode(date));
				card.appendChild(card_title);
				
				open_button = document.createElement("a");
				open_button.className = "btn btn-gossamer btn-small btn-primary";
				open_button.textContent = "Open";
				
				// Sets the event on click on the button
				open_button.addEventListener("click", () => {
					self.title.style.display = "none";
					self.dates.style.display = "none";
					registration.show(_name, date);
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
    
    function Registration(
		_div,
		_registration) {
		
		this.div = _div;
		this.registration = _registration;

        var self = this;

        this.show = function(name, date) {
            // Requests and updates with the results
            makeCall("GET", 'GoToExamOutcome?courseName=' + name + '&examDate=' + date, null, (req) => {
                switch(req.status) {
                    case 200:
                        var data = JSON.parse(req.responseText);
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
            self.registration.style.display = "none";
            self.registration.innerHTML = "";
            homeTitle.style.display = "none";
            homeTitle.innerHTML = "";
			let card, card_title, h2, open_button, b;
			
			card = document.createElement("div");
			card.className = "card card-blue";
				
			card_title = document.createElement("div");
			card_title.className = "card-title";
			h2 = document.createElement("h2");
			if(_data.judgment === "uninsered" || _data.judgment === "insered") {
				h2.textContent = "Grade not yet defined.";
				card_title.appendChild(h2);
			}
			if(_data.judgment === "published" || _data.judgment === "verbalised") {
				card_title.style.paddingBottom = "20px";
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
				
				if (_data.judgment === "verbalised") {
					h2.textContent = "The grade was verbalised.";
					card_title.appendChild(h2);
				}
				
				if (!(_data.judgment === null || _data.judgment === "absent" || _data.judgment === "failed" || _data.judgment === "tried again" ||
				 _data.judgment === "30L" || _data.judgment === "uninsered" || _data.judgment === "insered" || _data.judgment === "declined" || _data.judgment === "verbalised")) {
					 open_button = document.createElement("a");
					open_button.className = "btn btn-gossamer btn-medium btn-primary";
					open_button.textContent = "Decline Grade";
					
					open_button.addEventListener("click", () => {
						makeCall("POST", 'GoToDeclineGrade?courseName=' + _date.exam.course.name + '&examDate=' + _data.exam.date, null, (req) => {
			                switch(req.status) {
			                    case 200:
			                        self.show(_date.exam.course.name, _data.exam.date);
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
					
					card_title.appendChild(open_button);
				}
				
			}
				
			card.appendChild(card_title);
				
			self.registration.appendChild(card);
				
			homeTitle.innerHTML = "Welcome to the exam outcome page,";
			homeTitle.style.display = "block";
			self.registration.style.display = "block";
			self.div.style.display = "block";
        };
    }
})();
