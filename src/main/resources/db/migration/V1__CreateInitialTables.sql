
CREATE TABLE excursion (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           name VARCHAR(255) NOT NULL,
                           destinations TEXT,
                           start_date DATE,
                           end_date DATE,
                           travel_agency VARCHAR(255) NOT NULL,
                           price DOUBLE NOT NULL
);

CREATE TABLE user (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      first_name VARCHAR(50) NOT NULL,
                      last_name VARCHAR(50) NOT NULL,
                      birth_date DATE,
                      email VARCHAR(255) NOT NULL,
                      password VARCHAR(255) NOT NULL,
                      gender VARCHAR(10)
);
