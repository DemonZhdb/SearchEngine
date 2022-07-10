CREATE TABLE IF NOT EXISTS site
(
    id INT NOT NULL AUTO_INCREMENT,
    status ENUM('INDEXING', 'INDEXED', 'FAILED') NOT NULL,
    status_time DATETIME NOT NULL,
    last_error TEXT,
    url VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS page
(
    id INT NOT NULL AUTO_INCREMENT,
    site_id INT NOT NULL,
    path TEXT NOT NULL,
    code INT NOT NULL,
    content MEDIUMTEXT NOT NULL,
    PRIMARY KEY(id),
    UNIQUE KEY Path_Site (path(300), site_id),
    FOREIGN KEY (site_id) REFERENCES site(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS field
(
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    selector VARCHAR(255) NOT NULL,
    weight FLOAT NOT NULL,
    PRIMARY KEY(id),
    UNIQUE (selector)
);


CREATE TABLE IF NOT EXISTS lemma
(
    id INT NOT NULL AUTO_INCREMENT,
    lemma VARCHAR(255) NOT NULL,
    frequency INT NOT NULL,
    site_id INT NOT NULL,
    PRIMARY KEY(id),
    UNIQUE KEY lemma_site (lemma,site_id),
    FOREIGN KEY (site_id) REFERENCES site(id) ON DELETE CASCADE
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS index_page
(
    id INT NOT NULL AUTO_INCREMENT,
    page_id INT NOT NULL,
    lemma_id INT NOT NULL,
    rank_lemma float NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY (page_id) REFERENCES  page(id) ON DELETE CASCADE,
    FOREIGN KEY (lemma_id) REFERENCES lemma(id) ON DELETE CASCADE

);
CREATE TABLE IF NOT EXISTS query_lemma
(
    id INT NOT NULL AUTO_INCREMENT,
    lemma VARCHAR(255) NOT NULL,
    lemma_id INT NOT NULL,
    PRIMARY KEY(id)
) ;
