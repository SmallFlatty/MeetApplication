# Meet Application (RPs Meets)

Repository: https://github.com/SmallFlatty/MeetApplication

**Meet Application (RPs Meets)** — a small pet project for creating and managing meetings (online/offline), simple room booking and role-based access.

---

## Tech stack
- **Backend:** Java 17+, Spring Boot, Spring Security (JWT), Spring Data JPA  
- **Database:** MySQL (recommended); H2 for quick dev  
- **Frontend:** Vue.js (Vue 2/3), Vue Router, Pinia or Vuex  
- **Build tools:** Maven (backend), npm / yarn (frontend)

---

## Key features
- JWT authentication (register / login)  
- Create / edit / delete meetings (title, start/end, participants)  
- Roles: **Admin**, **Worker**  
- Room/resource booking  
- Simple email notification hooks (SMTP integration)  
- REST API + SPA frontend (Vue.js)

---

## Quickstart (local)

### Prerequisites
- Java 17+ (JDK)  
- Maven (or use included `./mvnw`)  
- Node.js 16+ and npm (or yarn)  
- MySQL server (or H2 for quick dev)  
- Git

### Clone repository
```
git clone https://github.com/SmallFlatty/MeetApplication.git
cd MeetApplication
```
### Backend (development)
```mvn spring-boot:run```

### Frontend (development)
```
cd frontend
npm install
npm run dev
```

### Minimal env example (backend)
```
# Server
server.port=8080
spring.profiles.active=local

# Database (MySQL)
spring.datasource.url=jdbc:mysql://localhost:3306/meeting_app
spring.datasource.username=your_db_name
spring.datasource.password=your_db_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

# Mail (Gmail SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_mail
spring.mail.password=your_mail_password
spring.mail.protocol=smtp
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
