CREATE TABLE complaints
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    created_at        timestamp NULL,
    updated_at        timestamp NULL,
    room_id           BIGINT NULL,
    member_id         BIGINT NULL,
    complaint_message VARCHAR(255) NOT NULL,
    status            VARCHAR(255) NULL,
    complaint_status  VARCHAR(255) NULL,
    completed_message VARCHAR(255) NULL,
    CONSTRAINT pk_complaints PRIMARY KEY (id)
);

ALTER TABLE complaints
    ADD CONSTRAINT FK_COMPLAINTS_ON_MEMBER FOREIGN KEY (member_id) REFERENCES members (id);

ALTER TABLE complaints
    ADD CONSTRAINT FK_COMPLAINTS_ON_ROOM FOREIGN KEY (room_id) REFERENCES rooms (id);