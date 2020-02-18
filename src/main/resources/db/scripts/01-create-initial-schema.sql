CREATE TABLE IF NOT EXISTS afisha_cinemas (
	"id" serial NOT NULL PRIMARY KEY,
	"name" varchar(100) NOT NULL UNIQUE,
	"link_afisha" varchar(100) NOT NULL,
	"link_about" varchar(100) NOT NULL,
	"created_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	"modified_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS afisha_movies (
	"id" serial NOT NULL PRIMARY KEY,
	"name" varchar(255) NOT NULL,
	"genre" varchar(50) NOT NULL,
	"link" varchar(100) NOT NULL,
	"image" varchar(100) NULL,
	"created_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	"modified_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS afisha_cinemas_movies (
	"cinema_id" int4 NOT NULL,
	"movie_id" int4 NOT NULL,
	"movie_date" DATE NOT NULL,
	"movie_time" TIME NOT NULL,
	"format" varchar(10),
	PRIMARY KEY (cinema_id,movie_id,movie_date,movie_time),
	FOREIGN KEY (cinema_id) REFERENCES afisha_cinemas(id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (movie_id) REFERENCES afisha_movies(id) ON DELETE CASCADE ON UPDATE CASCADE
);