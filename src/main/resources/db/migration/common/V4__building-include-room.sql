CREATE TABLE buildings_room_list
(
    building_id  BIGINT NOT NULL,
    room_list_id BIGINT NOT NULL
);

ALTER TABLE buildings_room_list
    ADD CONSTRAINT uc_buildings_room_list_roomlist UNIQUE (room_list_id);

ALTER TABLE buildings_room_list
    ADD CONSTRAINT fk_buiroolis_on_building FOREIGN KEY (building_id) REFERENCES buildings (id);

ALTER TABLE buildings_room_list
    ADD CONSTRAINT fk_buiroolis_on_room FOREIGN KEY (room_list_id) REFERENCES rooms (id);

ALTER TABLE rooms
DROP COLUMN `usage`;

ALTER TABLE rooms
    ADD room_usage VARCHAR(255) NULL;