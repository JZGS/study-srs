CREATE SEQUENCE IF NOT EXISTS vocabulary_word_id_seq;

CREATE TABLE IF NOT EXISTS vocabulary(
	word_id INTEGER DEFAULT vocabulary_word_id_seq.nextval PRIMARY KEY,
	word VARCHAR(30) NOT NULL,
	reading VARCHAR(50) NOT NULL,
	meaning VARCHAR(255) NOT NULL,
	UNIQUE (word, reading)
);