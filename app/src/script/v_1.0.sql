CREATE TABLE "video_chapter" (
  "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  "name" TEXT(500),
  "url" TEXT(2000),
  "contentAbstract" TEXT
);

CREATE TABLE "book_resource" (
"id"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
"name"  TEXT(500),
"author"  TEXT(100),
"url"  TEXT(2000),
"contentAbstract"  TEXT,
"last_reading"  INTEGER DEFAULT 1
);

CREATE TABLE "video_chapter" (
  "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  "name" TEXT(500),
  "url" TEXT(2000),
  "contentAbstract" TEXT
);

CREATE TABLE "video_resource" (
  "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  "name" TEXT(500),
  "url" TEXT(2000),
  "contentAbstract" TEXT
);