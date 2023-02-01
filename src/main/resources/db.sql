-- DROP table team;
-- drop table player;
--
CREATE TABLE team(
    id BIGINT  PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    bank DECIMAL NOT NULL
);

CREATE TABLE player(
    id BIGINT  PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    age INT,
    carrier_start INT,
    team_id BIGINT,
    FOREIGN KEY (team_id) REFERENCES team
);
