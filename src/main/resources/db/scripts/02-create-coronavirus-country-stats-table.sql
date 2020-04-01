CREATE TABLE IF NOT EXISTS corona_country_stats (
	"country" varchar(100) NOT NULL PRIMARY KEY,
	"total_cases" int4 NOT NULL DEFAULT 0,
	"new_cases" int4 NOT NULL DEFAULT 0,
	"total_deaths" int4 NOT NULL DEFAULT 0,
	"new_deaths" int4 NOT NULL DEFAULT 0,
	"total_recovered" int4 NOT NULL DEFAULT 0,
	"active_cases" int4 NOT NULL DEFAULT 0,
	"serious_cases" int4 NOT NULL DEFAULT 0,
	"total_cases_by_million" numeric(10,2) NOT NULL DEFAULT 0.0,
	"deaths_by_million" numeric(10,2) NOT NULL DEFAULT 0.0,
	"first_case_date" varchar(50) NOT NULL,
	"created_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	"modified_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);