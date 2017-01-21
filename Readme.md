This project is part of MOOC cyber-security-base course.

This document describers security flaws found from web application aimed for
online registration for free poetry lesson.

Application is simple and easy to use having only minimal functionalities without
need for any credentials from end user.

**APPLICATION CONTENT:**

  * main page (/) and all unknown pages redirected to /form
  * registration form (/form)
    * user gives "name" and "address" for registration
    * user can see how many persons have registered
  * confirm action (/confirm)
    * web backend ask from user that information is correct, user can choose confirm/cancel
  * action performed (/done)
    * confirms succesfull registration or cancellation to the user
  * Administration view (/admin)
    * Requires authentication (admin/president)
    * shows all users and gives possibility to delete registrations

**SECURITY FLAWS** [see more](https://www.owasp.org/index.php/Top_10_2013-Top_10)							  

**A2-Broken Session Management**

1. Start backend web application
2. Go to URL: /form -> fill form: John Smith/Texas -> submit
3. Check page source and you can see that cancellation is done based on URL 
   /cancel (POST). It clearly requires session to cancel the registration
4. Confirm registration and succesfull registration is informed, go back to main page
5. Now using direct reference to URL: /cancel, you can still cancel the registration.
   So session is not properly invalidated. This is vulnerability even without
   session real highjacking because in public computer next user can do it.   
   
HOW TO FIX:
HttpSession shall be invalidated properly in places where session data is not needed
anymore. In this application it means session shall be invalidated after action confirmation
or cancelling registration.

**A3-XSS**

1. Start backend web application
2. Go to URL: /form -> fill form with "SCRIPT TAG alert("XSS")l SCRIPT TAG" -> submit
3. Information is shown in confirmation page, it seems to be escaped correctly
4. Exploit comes in /admin pages which shows all information. 
   Possible malicious script gets executed for admin.

HOW TO FIX:
All text fields having external input data shall be escaped. In Thymeleaf it can be done
with th:text tag. In this application fix shall be in done in admin.html template.

**A4-Insecure Direct Object References**

1. Start backend web application
2. Go to URL: /form -> fill form: John Smith/Texas -> submit + confirm
3. Go to URL: /admin and you can see that authentication is required, DO NOT AUTHENTICATE
4. Go to URL: /admin/delete?reqId=1
5. Admin view of the user is shown without authentication

HOW TO FIX:

Security configuration shall apply for whole admin application. 
http.authorizeRequests().antMatchers("/admin/**").authenticated()

**A6-Sensitive Data Exposure**

1. Start backend web application
2. Monitor interface, like 127.0.0.1, with Wireshark
3. Go to URL: /admin and you get forwarded to /login
4. Enter credentials
5. From Wireshark capture you can see that login credentials are transferred in plain text

HOW TO FIX:

Use HTTPS with authorized certificates

**A8-Cross-Site Request Forgery (CSRF)**

1. Start backend web application
2. Go to URL: /form -> fill form: John Smith/Texas -> submit
3. Go to URL: /admin and log in
4. Now admin is authorized and authenticated so CSRF can happen so that
   the following commands are executed in malicious web site
   http://127.0.0.1:8080/admin/delete?regId=1
5. Registered user is deleted without anybody noting it

HOW TO FIX:

  * Require re-authentication or
  * Set unpredictable token to each HTTP request. In Spring this can be done by not disabling csrf in security configs.

**A5-Security Misconfiguration**

1. Start backend web application
2. Go to unknown URL: /form/*
3. Not formated error page is returned, which gives indication to scan information even more

HOW TO FIX:

Include proper error page similar than theme in web pages. Show only relevant information
for end user related to their action they have made.

**Plus**

  * All input validations are missing
  * Error output is inconsistent and might give some too details information from the backend application


