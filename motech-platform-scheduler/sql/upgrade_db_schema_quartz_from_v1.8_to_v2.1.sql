--
-- DROP TABLES THAT ARE NO LONGER USED
--
DROP TABLE QRTZ_JOB_LISTENERS;
DROP TABLE QRTZ_TRIGGER_LISTENERS;
--
-- DROP COLUMNS THAT ARE NO LONGER USED
--
ALTER TABLE QRTZ_JOB_DETAILS DROP COLUMN IS_VOLATILE;
ALTER TABLE QRTZ_TRIGGERS DROP COLUMN IS_VOLATILE;
ALTER TABLE QRTZ_FIRED_TRIGGERS DROP COLUMN IS_VOLATILE;
--
-- ADD NEW COLUMNS THAT REPLACE THE 'IS_STATEFUL' COLUMN
--
ALTER TABLE QRTZ_JOB_DETAILS ADD COLUMN IS_NONCONCURRENT BOOL;
ALTER TABLE QRTZ_JOB_DETAILS ADD COLUMN IS_UPDATE_DATA BOOL;
UPDATE QRTZ_JOB_DETAILS SET IS_NONCONCURRENT = IS_STATEFUL;
UPDATE QRTZ_JOB_DETAILS SET IS_UPDATE_DATA = IS_STATEFUL;
ALTER TABLE QRTZ_JOB_DETAILS DROP COLUMN IS_STATEFUL;
ALTER TABLE QRTZ_FIRED_TRIGGERS ADD COLUMN IS_NONCONCURRENT BOOL;
ALTER TABLE QRTZ_FIRED_TRIGGERS ADD COLUMN IS_UPDATE_DATA BOOL;
UPDATE QRTZ_FIRED_TRIGGERS SET IS_NONCONCURRENT = IS_STATEFUL;
UPDATE QRTZ_FIRED_TRIGGERS SET IS_UPDATE_DATA = IS_STATEFUL;
ALTER TABLE QRTZ_FIRED_TRIGGERS DROP COLUMN IS_STATEFUL;
--
-- ADD NEW 'SCHED_NAME' COLUMN TO ALL TABLES
--
ALTER TABLE QRTZ_BLOB_TRIGGERS ADD COLUMN SCHED_NAME VARCHAR(120) NOT NULL DEFAULT 'MotechScheduler';
ALTER TABLE QRTZ_CALENDARS ADD COLUMN SCHED_NAME VARCHAR(120) NOT NULL DEFAULT 'MotechScheduler';
ALTER TABLE QRTZ_CRON_TRIGGERS ADD COLUMN SCHED_NAME VARCHAR(120) NOT NULL DEFAULT 'MotechScheduler';
ALTER TABLE QRTZ_FIRED_TRIGGERS ADD COLUMN SCHED_NAME VARCHAR(120) NOT NULL DEFAULT 'MotechScheduler';
ALTER TABLE QRTZ_JOB_DETAILS ADD COLUMN SCHED_NAME VARCHAR(120) NOT NULL DEFAULT 'MotechScheduler';
ALTER TABLE QRTZ_LOCKS ADD COLUMN SCHED_NAME VARCHAR(120) NOT NULL DEFAULT 'MotechScheduler';
ALTER TABLE QRTZ_PAUSED_TRIGGER_GRPS ADD COLUMN SCHED_NAME VARCHAR(120) NOT NULL DEFAULT 'MotechScheduler';
ALTER TABLE QRTZ_SCHEDULER_STATE ADD COLUMN SCHED_NAME VARCHAR(120) NOT NULL DEFAULT 'MotechScheduler';
ALTER TABLE QRTZ_SIMPLE_TRIGGERS ADD COLUMN SCHED_NAME VARCHAR(120) NOT NULL DEFAULT 'MotechScheduler';
ALTER TABLE QRTZ_TRIGGERS ADD COLUMN SCHED_NAME VARCHAR(120) NOT NULL DEFAULT 'MotechScheduler';
--
-- DROP ALL PRIMARY AND FOREIGN KEY CONSTRAINTS, SO THAT WE CAN DEFINE NEW ONES
--
ALTER TABLE QRTZ_TRIGGERS DROP FOREIGN KEY qrtz_triggers_ibfk_1;
ALTER TABLE QRTZ_BLOB_TRIGGERS DROP PRIMARY KEY;
ALTER TABLE QRTZ_BLOB_TRIGGERS DROP FOREIGN KEY qrtz_blob_triggers_ibfk_1;
ALTER TABLE QRTZ_SIMPLE_TRIGGERS DROP PRIMARY KEY;
ALTER TABLE QRTZ_SIMPLE_TRIGGERS DROP FOREIGN KEY qrtz_simple_triggers_ibfk_1;
ALTER TABLE QRTZ_CRON_TRIGGERS DROP PRIMARY KEY;
ALTER TABLE QRTZ_CRON_TRIGGERS DROP FOREIGN KEY qrtz_cron_triggers_ibfk_1;
ALTER TABLE QRTZ_JOB_DETAILS DROP PRIMARY KEY;
ALTER TABLE QRTZ_JOB_DETAILS ADD PRIMARY KEY (SCHED_NAME, JOB_NAME, JOB_GROUP);
ALTER TABLE QRTZ_TRIGGERS DROP PRIMARY KEY;
--
-- ADD ALL PRIMARY AND FOREIGN KEY CONSTRAINTS, BASED ON NEW COLUMNS
--
ALTER TABLE QRTZ_TRIGGERS ADD PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);
ALTER TABLE QRTZ_TRIGGERS ADD FOREIGN KEY (SCHED_NAME, JOB_NAME, JOB_GROUP) REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME, JOB_NAME, JOB_GROUP);
ALTER TABLE QRTZ_BLOB_TRIGGERS ADD PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);
ALTER TABLE QRTZ_BLOB_TRIGGERS ADD FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) REFERENCES QRTZ_TRIGGERS(SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);
ALTER TABLE QRTZ_CRON_TRIGGERS ADD PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);
ALTER TABLE QRTZ_CRON_TRIGGERS ADD FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) REFERENCES QRTZ_TRIGGERS(SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);
ALTER TABLE QRTZ_SIMPLE_TRIGGERS ADD PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);
ALTER TABLE QRTZ_SIMPLE_TRIGGERS ADD FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) REFERENCES QRTZ_TRIGGERS(SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);
ALTER TABLE QRTZ_FIRED_TRIGGERS DROP PRIMARY KEY;
ALTER TABLE QRTZ_FIRED_TRIGGERS ADD PRIMARY KEY (SCHED_NAME, ENTRY_ID);
ALTER TABLE QRTZ_CALENDARS DROP PRIMARY KEY;
ALTER TABLE QRTZ_CALENDARS ADD PRIMARY KEY (SCHED_NAME, CALENDAR_NAME);
ALTER TABLE QRTZ_LOCKS DROP PRIMARY KEY;
ALTER TABLE QRTZ_LOCKS ADD PRIMARY KEY (SCHED_NAME, LOCK_NAME);
ALTER TABLE QRTZ_PAUSED_TRIGGER_GRPS DROP PRIMARY KEY;
ALTER TABLE QRTZ_PAUSED_TRIGGER_GRPS ADD PRIMARY KEY (SCHED_NAME, TRIGGER_GROUP);
ALTER TABLE QRTZ_SCHEDULER_STATE DROP PRIMARY KEY;
ALTER TABLE QRTZ_SCHEDULER_STATE ADD PRIMARY KEY (SCHED_NAME, INSTANCE_NAME);
--
-- ADD NEW SIMPROP_TRIGGERS TABLE
--
CREATE TABLE QRTZ_SIMPROP_TRIGGERS
 (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    STR_PROP_1 VARCHAR(512) NULL,
    STR_PROP_2 VARCHAR(512) NULL,
    STR_PROP_3 VARCHAR(512) NULL,
    INT_PROP_1 INT NULL,
    INT_PROP_2 INT NULL,
    LONG_PROP_1 BIGINT NULL,
    LONG_PROP_2 BIGINT NULL,
    DEC_PROP_1 NUMERIC(13,4) NULL,
    DEC_PROP_2 NUMERIC(13,4) NULL,
    BOOL_PROP_1 BOOL NULL,
    BOOL_PROP_2 BOOL NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
	ENGINE=InnoDB;
--
-- CREATE INDEXES FOR FASTER QUERIES
--
CREATE INDEX IDX_QRTZ_J_REQ_RECOVERY ON QRTZ_JOB_DETAILS(SCHED_NAME,REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_J_GRP ON QRTZ_JOB_DETAILS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_J ON QRTZ_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_JG ON QRTZ_TRIGGERS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_C ON QRTZ_TRIGGERS(SCHED_NAME,CALENDAR_NAME);
CREATE INDEX IDX_QRTZ_T_G ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_T_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_G_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NEXT_FIRE_TIME ON QRTZ_TRIGGERS(SCHED_NAME,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE_GRP ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_FT_TRIG_INST_NAME ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME);
CREATE INDEX IDX_QRTZ_FT_INST_JOB_REQ_RCVRY ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_FT_J_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_JG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_T_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_FT_TG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);
