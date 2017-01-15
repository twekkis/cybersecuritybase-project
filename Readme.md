This project is part of MOOC cyber-security-base course.

This document describers security flaws found from web application aimed for
online registration for free poetry lesson.

Application is simple and easy to use having only minimal functionalities without
need for any credentials from end user.

**APPLICATION CONTENT:**

  * main page (/)
    * user can register or modify existing registration (only removing supported)
  * registration form (/form)
    * user gives "name" and "address" for registration
  * confirm action (/confirm)
    * web backend ask from user that information is correct, user can choose confirm/cancel
    * after confirmation content is shown to user + registration id
  * modification form (/modify)
    * user gives "registration id", "name" and "address" to search data
    * "registration id", "name" and "address" are shown, user can cancel regisration (show)
  * Administration view (/admin)
    * Requires authentication (admin/president)
    * shows all users and gives possibility to cancel registrations

**SECURITY FLAWS** [see more](https://www.owasp.org/index.php/Top_10_2013-Top_10)							  

**A2-Broken Session Management**

1. Start backend web application
2. Go to URL: / -> select "Register" -> fill form: John Smith/Texas -> confirm
3. Check given registration id
4. Go to URL: /modify -> fill form: registration id/John Smith/Texas
5. Data successfully shown
6. Check page source and you can see that cancellation is done based on URL 
   /cancel. It clearly requires session to cancel the registration
7. Instead of cancelling registration, go back to main page
8. Using direct reference to URL: /cancel, you can still cancel the registration.
   So session is not properly invalidated. This is vulnerability even without
   session real highjacking because in public computer next user can do it.   
   
HOW TO FIX:
HttpSession shall be invalidated properly in places where session data is not needed
anymore. In this application it means after action confirmation, cancelling or when 
returning back to main page.

**A3-XSS**

1. Start backend web application
2. Go to URL: / -> select "Register" -> fill form with "lt;scriptgt;alert("XSS")lt;/scriptgt;" -> confirm
3. Script is executed in confirmation pages etc. Can be assumed that attacker can tolerate that.
4. Exploit comes in /admin pages which shows all information or selects some user for modify. 
   Possible malicious script gets executed for admin.

HOW TO FIX:
All text fields having external input data shall be escaped. In Thymeleaf it can be done
with th:utext tag.

**A4-Insecure Direct Object References**

1. Start backend web application
2. Go to URL: / -> select "Register" -> fill form: John Smith/Texas -> confirm
3. Go to URL: /admin and you can see that authentication is required, DO NOT AUTHENTICATE
4. Go to URL: /admin/show?reqId=1
5. Admin view of the user is shown without authentication

HOW TO FIX:

Security configuration shall apply for whole admin application. 
http.authorizeRequests().antMatchers("/admin/**").authenticated()

**A6-Sensitive Data Exposure**

case 1)
1. Start backend web application
2. Monitor interface, like 127.0.0.1, with Wireshark
3. Go to URL: /admin and you get forwarded to /login
4. Enter credentials
5. From Wireshark capture you can see that login credentials are transferred in plain text

case 2)
1. Start backend web application
2. Monitor interface, like 127.0.0.1, with Wireshark
3. Go to URL: / -> select "Register" -> fill form: John Smith/Texas -> confirm
4. From Wireshark capture you can see that registration number is transferred in plain text.
   That number is used in URL: /modify as a search key

HOW TO FIX:

Use HTTPS with authorized certificates

**A8-Cross-Site Request Forgery (CSRF)**

1. Start backend web application
2. Go to URL: / -> select "Register" -> fill form: John Smith/Texas -> confirm
3. Go to URL: /admin and log in
4. Now admin is authorized and authenticated so CSRF can happen so that
   the following commands are executed in malicious web site
   http://127.0.0.1:8080/admin/show?regId=1
   http://127.0.0.1:8080/cancel
5. Registered user is deleted without anybody noting it

HOW TO FIX:

  * Require re-authentication or
  * Set unpredictable token to each HTTP request. In Spring this can be done by not disabling csrf in security configs.








