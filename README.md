# ReimbursementApp
Java EE application to manage reimbursement of expenses after user business trip.

## Environment setup
1. Clone repository or download zip and extract it.
2. Create new project in your favourite IDE using cloned repository as its directory root.
3. If needed refresh/reopen the project.
4. Setup Java Servlet server and integrate it with your IDE.
5. Setup MySQL server.
6. Import database using initDB.sql file.
7. Edit dbConifg.cfg file using credentials to your MySQL server.

## Building from Source
In project directory call:

`mvn install`

If you want to skip tests add param: `-Dmaven.test.skip`

## Testing
Before tests make sure MySQL server is running and reimbursement database exists.

Then in project directory call:

`mvn test`