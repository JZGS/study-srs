CREATE SEQUENCE IF NOT EXISTS kanjis_kanji_id_seq;

CREATE TABLE IF NOT EXISTS kanjis(
	kanji_id SMALLINT DEFAULT kanjis_kanji_id_seq.nextval PRIMARY KEY,
	kanji CHAR(1) NOT NULL UNIQUE,
	meaning VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS 	kanjis_confusions(
	kanji_id SMALLINT NOT NULL,
	confusion_id SMALLINT NOT NULL,
	CONSTRAINT fk_kanji_id FOREIGN KEY (kanji_id)
		REFERENCES kanjis(kanji_id) ON DELETE CASCADE,
	CONSTRAINT fk_confusion_id FOREIGN KEY (confusion_id)
		REFERENCES kanjis(kanji_id) ON DELETE CASCADE,
	UNIQUE (kanji_id, confusion_id),
	CONSTRAINT chk_confusion_prior CHECK (kanji_id > confusion_id)
);