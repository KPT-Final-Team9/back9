ALTER TABLE settings
    ADD encourage_message VARCHAR(255) NULL;

ALTER TABLE settings
DROP
COLUMN message;