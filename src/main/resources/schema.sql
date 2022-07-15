DO
'
BEGIN
    IF to_regtype( ''status_enum'') IS NULL THEN
        CREATE TYPE status_enum AS ENUM(''INDEXING'', ''INDEXED'', ''FAILED'');
    END IF;
END ;
' language plpgsql;


CREATE TABLE IF NOT EXISTS site
(
    id SERIAL PRIMARY KEY NOT NULL,
    status status_enum NOT NULL,
    status_time Timestamp NOT NULL,
    last_error VARCHAR(255),
    url VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS page
(
    id SERIAL PRIMARY KEY NOT NULL,
    site_id INTEGER NOT NULL,
    path VARCHAR(512) NOT NULL,
    code INTEGER NOT NULL,
    content TEXT NOT NULL,
    CONSTRAINT  Path_Site UNIQUE  (path, site_id),
    FOREIGN KEY (site_id) REFERENCES site(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS field
(
    id SERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    selector VARCHAR(255) NOT NULL,
    weight FLOAT NOT NULL,
    UNIQUE (selector)
);


CREATE TABLE IF NOT EXISTS lemma
(
    id SERIAL PRIMARY KEY NOT NULL,
    lemma VARCHAR(255) NOT NULL,
    frequency INTEGER NOT NULL,
    site_id INTEGER NOT NULL,
    CONSTRAINT lemma_site  UNIQUE (lemma,site_id),
    FOREIGN KEY (site_id) REFERENCES site(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS index_page
(
    id SERIAL PRIMARY KEY NOT NULL,
    page_id INTEGER NOT NULL,
    lemma_id INTEGER NOT NULL,
    rank_lemma FLOAT NOT NULL,
    FOREIGN KEY (page_id) REFERENCES  page(id) ON DELETE CASCADE,
    FOREIGN KEY (lemma_id) REFERENCES lemma(id) ON DELETE CASCADE

);
CREATE TABLE IF NOT EXISTS query_lemma
(
    id SERIAL PRIMARY KEY NOT NULL,
    lemma VARCHAR(255) NOT NULL,
    lemma_id INTEGER NOT NULL
);
