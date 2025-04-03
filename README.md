# Software Testing - Software Quiz Application

## Project Description 📚
This application is a software-related quiz where users can register with their email and take a quiz consisting of ten software-related questions. Each question is worth 2 points, and the quiz has a time limit. After completing the quiz, users are directed to the results page where they can view their score and status. Additionally, users can print their results, download them as a PDF, and log in with their email and password to review their quiz history. The application also features a leaderboard ranking scores from highest to lowest, supports dark and light mode, and offers bilingual support for English and Persian.

## Database 🧐
The project utilizes multiple databases for different functionalities:
1. **MySQL** - Stores user information and quiz scores.
2. **SharedPreferences & SQLite** - Manages theme and language preferences.

### users Table 👌
This table stores user registration information.

| Column  | Data Type    | Description                      |
|---------|-------------|----------------------------------|
| id      | int(11)     | Auto-incremented unique ID      |
| name    | varchar(50) | User's first name               |
| surname | varchar(50) | User's surname                  |
| email   | varchar(100)| User's email (must be unique)   |
| password| varchar(255)| User's password (hashed)        |

### scores Table 📊
This table stores users' quiz results.

| Column     | Data Type    | Description                           |
|------------|-------------|---------------------------------------|
| id         | int(11)     | Auto-incremented unique ID           |
| name       | varchar(255)| User's first name                    |
| surname    | varchar(255)| User's surname                       |
| email      | varchar(255)| User's email (must be unique)        |
| score      | int(11)     | User's quiz score (calculated)       |
| created_at | timestamp   | Timestamp of when the score was recorded |

## Database Setup 🧪
**Import the Database:**
- Use the provided SQL dump file to create the database and tables in your MySQL server.
- The `users` and `scores` tables will be created with the necessary columns, including primary keys and unique indexes for the email column.

**Database Connection Setup:**
- Update the database connection details in `server/db_config.php` to match your MySQL server settings.

## Installation and Setup 🚀
### To Run the Android App:
1. Clone the project from GitHub.
2. Open the project files in Android Studio.
3. Connect your Android device and run the project.

### To Set Up the Server:
1. Install PHP and MySQL on your server.
2. Import the provided SQL dump to create the database and tables.
3. Update the database connection in `server/db_config.php`.
4. Upload the PHP scripts to your server.

## Features 🚀
- **User Authentication:** Register and log in with an email and password.
- **Quiz System:** 10 software-related questions, each worth 2 points, with a countdown timer.
- **Result Page:** Displays user’s name, surname, email, and score.
- **PDF Download & Print:** Users can download their results as a PDF and print them.
- **Leaderboard:** Shows top scores ranked from highest to lowest.
- **Dark Mode & Light Mode:** Users can switch between themes.
- **Multi-Language Support:** English and Persian (stored in `SharedPreferences` or `SQLite`).

## Usage 📲
1. **Page 1:** Register/Login with name, surname, email, and password.
2. **Page 2:** Answer 10 software-related questions within the time limit (2 points per question).
3. **Page 3:** View name, surname, email, score, and status (pass/fail).
4. **Leaderboard:** View top scores from highest to lowest.
5. **Settings:** Change theme (Dark/Light) and language (English/Persian).

This project is designed to provide an interactive and structured quiz experience with modern features. 🌟

