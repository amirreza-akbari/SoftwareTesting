#  Software Testing  - A Software Quiz Application

## Project Description

This application is a quiz about software. It collects the user's name, surname, and email, then proceeds to the next page where ten specialized software-related questions are asked. Each question is worth 2 points and is time-limited. The scores are saved in a MySQL database, and the results are displayed on the third page.

## Project Structure

- **app/**: All files related to the Android application.
- **server/**: PHP scripts to save and retrieve data from the database.
- **docs/**: Project documentation and installation guides.
- **assets/**: Images and graphical resources.

## Installation and Setup

### To run the Android app:
1. Clone the project from GitHub.
2. Open the files in Android Studio.
3. Connect your Android device and run the project.

### To set up the server:
1. Install PHP and MySQL.
2. Configure the database connection in `server/db_config.php`.
3. Upload the PHP scripts to your server.

## Usage

1. On the first page, enter your name, surname, and email.
2. Proceed to the next page and answer the questions.
3. Your score will be calculated and stored in the database. On the third page, you will see your name, surname, email, and score.

## License

[Insert your license here]

