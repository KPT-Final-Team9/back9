CREATE TABLE alarms
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_at    timestamp NULL,
    updated_at    timestamp NULL,
    received_id   BIGINT       NOT NULL,
    alarm_type    VARCHAR(255) NOT NULL,
    read_status   BIT(1)       NOT NULL,
    status        VARCHAR(255) NOT NULL,
    alarm_title   VARCHAR(255) NULL,
    alarm_message VARCHAR(255) NULL,
    CONSTRAINT pk_alarms PRIMARY KEY (id)
);