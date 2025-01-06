# Software Testing - A Software Quiz Application

## Project Description

This application is a quiz about software. It collects the user's name, surname, and email, then proceeds to the next page where ten specialized software-related questions are asked. Each question is worth 2 points and is time-limited. The scores are saved in a MySQL database, and the results are displayed on the third page.

## Project Structure

- **app/**: All files related to the Android application.
- **server/**: PHP scripts to save and retrieve data from the database.
- **docs/**: Project documentation and installation guides.
- **assets/**: Images and graphical resources.

## Database

The project uses two tables in a MySQL database: **`users`** and **`scores`**.

### `users` Table

This table stores the registration information of users:

| Column    | Data Type    | Description                          |
|-----------|--------------|--------------------------------------|
| `id`      | int(11)      | Auto-incremented ID                  |
| `name`    | varchar(50)  | User's first name                    |
| `surname` | varchar(50)  | User's surname                       |
| `email`   | varchar(100) | User's email (unique)                |

### `scores` Table

This table stores the quiz results of the users:

| Column     | Data Type    | Description                          |
|------------|--------------|--------------------------------------|
| `id`       | int(11)      | Auto-incremented ID                  |
| `name`     | varchar(255) | User's first name                    |
| `surname`  | varchar(255) | User's surname                       |
| `email`    | varchar(255) | User's email (unique)                |
| `score`    | int(11)      | User's quiz score                    |
| `created_at` | timestamp  | Timestamp when the score was saved  |

### Database Setup

1. **Import the database**: 
   - Use the provided SQL dump file to import the database structure into your MySQL server.
   - The tables `users` and `scores` are created with necessary columns, including primary keys and unique indexes on the `email` column to prevent duplicates.

2. **Configure the database connection**: 
   - Update the database connection details in the `server/db_config.php` file to match your MySQL server settings.

## Installation and Setup

### To run the Android app:
1. Clone the project from GitHub.
2. Open the files in Android Studio.
3. Connect your Android device and run the project.

### To set up the server:
1. Install PHP and MySQL.
2. Import the SQL dump provided to create the necessary database and tables.
3. Configure the database connection in `server/db_config.php`.
4. Upload the PHP scripts to your server.

## Usage

1. On the first page, enter your name, surname, and email.
2. Proceed to the next page and answer the questions.
3. Your score will be calculated and stored in the database. On the third page, you will see your name, surname, email, and score.

## License

[Insert your license here]
