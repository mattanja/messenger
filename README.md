messenger
=========

mailing list manager based on play! framework

## Frameworks used & additional info

 * Play! framework >= 2.1.1 http://www.playframework.com/
 * TODO:
  * Script dependencies etc... https://github.com/ded/script.js
  * http://angularjs.org/
  * http://angular-ui.github.com/
  * http://angular-ui.github.com/bootstrap/


## API documentation

### Lists
GET      /lists                controllers.List.index
POST     /list/newList         controllers.List.newList
GET      /list/:email          controllers.List.detail(email: String)
POST     /list/update/:email   controllers.List.update(email: String)

BODY:
{"email":"messenger@kernetics.de", "addMembers": [{"email":"hallo123","name":"","password":""},{"email":"hallo456","name":"","password":""}], "removeMembers":[]}

POST	 /list/delete/:email   controllers.List.delete(email: String)

### Users / Members
GET      /users                controllers.User.index
POST     /user/newUser         controllers.User.newUser
GET      /user/:email          controllers.User.detail(email: String)
POST     /user/update/:email   controllers.User.update(email: String)
POST     /user/delete/:email   controllers.User.delete(email: String)
