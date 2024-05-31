ALTER TABLE rooms
    ADD represent BIT(1) NULL;

UPDATE rooms
SET represent = 0;