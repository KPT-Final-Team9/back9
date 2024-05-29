CREATE TABLE buildings
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at timestamp NULL,
    updated_at timestamp NULL,
    name       VARCHAR(255) NULL,
    address    VARCHAR(255) NULL,
    zip_code   VARCHAR(255) NULL,
    status     VARCHAR(255) NULL,
    CONSTRAINT pk_buildings PRIMARY KEY (id)
);

CREATE TABLE rooms
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    created_at  timestamp NULL,
    updated_at  timestamp NULL,
    name        VARCHAR(255) NULL,
    floor       VARCHAR(255) NULL,
    area        FLOAT NULL,
    room_status VARCHAR(255) NULL,
    `usage`     VARCHAR(255) NULL,
    rating      FLOAT NULL,
    building_id BIGINT NULL,
    member_id   BIGINT NULL,
    status      VARCHAR(255) NULL,
    CONSTRAINT pk_rooms PRIMARY KEY (id)
);

CREATE TABLE tenants
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    created_at     timestamp NULL,
    updated_at     timestamp NULL,
    name           VARCHAR(255) NULL,
    company_number VARCHAR(255) NULL,
    status         VARCHAR(255) NULL,
    CONSTRAINT pk_tenants PRIMARY KEY (id)
);

CREATE TABLE members
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    created_at         timestamp NULL,
    updated_at         timestamp NULL,
    email              VARCHAR(255) NULL,
    `role`             VARCHAR(255) NULL,
    phone_number       VARCHAR(255) NULL,
    status             VARCHAR(255) NULL,
    sign_type          VARCHAR(255) NULL,
    firebase_uid       VARCHAR(255) NULL,
    firebase_fcm_token VARCHAR(255) NULL,
    tenant_id          BIGINT NULL,
    CONSTRAINT pk_members PRIMARY KEY (id)
);

CREATE TABLE contracts
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    created_at      timestamp NULL,
    updated_at      timestamp NULL,
    start_date      date         NOT NULL,
    end_date        date         NOT NULL,
    check_out       date         NOT NULL,
    deposit         BIGINT       NOT NULL,
    rental_price    BIGINT       NOT NULL,
    contract_status VARCHAR(255) NOT NULL,
    room_id         BIGINT NULL,
    tenant_id       BIGINT NULL,
    status          VARCHAR(255) NULL,
    CONSTRAINT pk_contracts PRIMARY KEY (id)
);

CREATE TABLE scores
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    created_at       timestamp NULL,
    updated_at       timestamp NULL,
    score            INT          NOT NULL,
    comment          VARCHAR(255) NOT NULL,
    bookmark         BIT(1)       NOT NULL,
    rating_type      VARCHAR(255) NOT NULL,
    room_id          BIGINT NULL,
    tenant_member_id BIGINT NULL,
    status           VARCHAR(255) NULL,
    CONSTRAINT pk_scores PRIMARY KEY (id)
);

CREATE TABLE settings
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_at    timestamp NULL,
    updated_at    timestamp NULL,
    rating_toggle BIT(1) NULL,
    message       VARCHAR(255) NULL,
    room_id       BIGINT NULL,
    status        VARCHAR(255) NULL,
    CONSTRAINT pk_settings PRIMARY KEY (id)
);

ALTER TABLE contracts
    ADD CONSTRAINT uc_contracts_room UNIQUE (room_id);

ALTER TABLE contracts
    ADD CONSTRAINT uc_contracts_tenant UNIQUE (tenant_id);

ALTER TABLE settings
    ADD CONSTRAINT uc_settings_room UNIQUE (room_id);

ALTER TABLE contracts
    ADD CONSTRAINT FK_CONTRACTS_ON_ROOM FOREIGN KEY (room_id) REFERENCES rooms (id);

ALTER TABLE contracts
    ADD CONSTRAINT FK_CONTRACTS_ON_TENANT FOREIGN KEY (tenant_id) REFERENCES tenants (id);

ALTER TABLE scores
    ADD CONSTRAINT FK_SCORES_ON_ROOM FOREIGN KEY (room_id) REFERENCES rooms (id);

ALTER TABLE scores
    ADD CONSTRAINT FK_SCORES_ON_TENANT_MEMBER FOREIGN KEY (tenant_member_id) REFERENCES members (id);

ALTER TABLE members
    ADD CONSTRAINT FK_MEMBERS_ON_TENANT FOREIGN KEY (tenant_id) REFERENCES tenants (id);

ALTER TABLE rooms
    ADD CONSTRAINT FK_ROOMS_ON_BUILDING FOREIGN KEY (building_id) REFERENCES buildings (id);

ALTER TABLE rooms
    ADD CONSTRAINT FK_ROOMS_ON_MEMBER FOREIGN KEY (member_id) REFERENCES members (id);

ALTER TABLE settings
    ADD CONSTRAINT FK_SETTINGS_ON_ROOM FOREIGN KEY (room_id) REFERENCES rooms (id);