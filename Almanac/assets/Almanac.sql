BEGIN TRANSACTION;
DELETE FROM sqlite_sequence;
INSERT INTO "sqlite_sequence" VALUES('Saints',3);
CREATE TABLE "Saints" (
    "ID" INTEGER PRIMARY KEY AUTOINCREMENT,
    "SaintDate" TEXT NOT NULL,
    "SaintName" TEXT NOT NULL,
    "SaintDescription" TEXT NOT NULL
);
INSERT INTO "Saints" VALUES(1,'01/01','Maria Santissima Madre di Dio','Maria Santissima Madre di Dio');
INSERT INTO "Saints" VALUES(2,'02/01','Santi Basilio Magno e Gregorio Nazianzeno','Vescovi e dottori della Chiesa');
INSERT INTO "Saints" VALUES(3,'03/01','Santissimo Nome di Gesù','Santissimo Nome di Gesù');
COMMIT;
