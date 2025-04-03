# **Software Testing - Software Quiz Application** 🎓   

## **📚 Project Overview**  
This application is a **software-related quiz** where users can **register with their email**, take a **10-question quiz**, and track their progress. Each question is worth **2 points**, and the quiz is **time-limited**. After completing the quiz, users are redirected to the **results page**, where they can view their **score and status**.  

### **✨ Key Features:**  
✅ **User Authentication:** Secure login & registration with email and password.  
✅ **Engaging Quiz Experience:** 10 carefully curated **software-related questions** with a countdown timer.  
✅ **Smart Result Tracking:** Users can **print**, **download as PDF**, and review past quiz attempts.  
✅ **Leaderboard System:** Displays **top scores ranked from highest to lowest**.  
✅ **Dark & Light Mode:** Users can switch between themes for **better visibility**.  
✅ **Multi-Language Support:** English & Persian (**stored in SharedPreferences & SQLite**).  

---

## **🗄 Database Structure**  
The project uses **multiple databases** for **different functionalities**:  
📌 **MySQL** → Stores user profiles & quiz scores.  
📌 **SharedPreferences & SQLite** → Manages theme & language preferences.  

### **👥 `users` Table (User Information)**  
This table stores **user registration details**.

| Column    | Data Type    | Description                           |
|-----------|-------------|---------------------------------------|
| id        | int(11)     | Auto-incremented unique ID           |
| name      | varchar(50) | User's first name                    |
| surname   | varchar(50) | User's surname                       |
| email     | varchar(100)| User's email (**must be unique**)    |
| password  | varchar(255)| User's password (**hashed**)         |

### **📊 `scores` Table (Quiz Results)**  
Stores each user’s **quiz score and timestamp**.

| Column     | Data Type    | Description                           |
|------------|-------------|---------------------------------------|
| id         | int(11)     | Auto-incremented unique ID           |
| name       | varchar(255)| User's first name                    |
| surname    | varchar(255)| User's surname                       |
| email      | varchar(255)| User's email (**must be unique**)    |
| score      | int(11)     | User's quiz score (calculated)       |
| created_at | timestamp   | Timestamp of when the score was recorded |

---

## **🔧 Database Setup**  
### **📥 Import the Database**  
1️⃣ Use the **provided SQL dump file** to create the database and tables in your **MySQL server**.  
2️⃣ The `users` and `scores` tables will be **automatically created** with primary keys and unique indexes.  

### **⚙️ Configure Database Connection**  
1️⃣ Open `server/db_config.php`.  
2️⃣ Update the **database connection settings** to match your MySQL server.  

---

## **🚀 Installation & Setup**  
### **📱 Running the Android App**  
1️⃣ Clone the **GitHub repository**.  
2️⃣ Open the project in **Android Studio**.  
3️⃣ Connect your **Android device** or use an **emulator** to run the app.  

### **🌐 Setting Up the Server**  
1️⃣ Install **PHP and MySQL** on your server.  
2️⃣ Import the provided **SQL dump file** to create the database.  
3️⃣ Update the database connection in **`server/db_config.php`**.  
4️⃣ Upload the **PHP scripts** to your server.  

---

## **🎯 How It Works**  
🟢 **Step 1:** Register or log in using **name, surname, email, and password**.  
🟢 **Step 2:** Answer **10 software-related questions** before the **countdown timer ends**.  
🟢 **Step 3:** View your **score, status (pass/fail), and quiz history**.  
🟢 **Step 4:** Access the **Leaderboard** to see **top-ranked users**.  
🟢 **Step 5:** Change **app theme (Dark/Light)** and switch between **English & Persian**.  

This **interactive quiz application** delivers an **engaging experience** with a **modern UI** and **powerful tracking features**! 🚀🌟
