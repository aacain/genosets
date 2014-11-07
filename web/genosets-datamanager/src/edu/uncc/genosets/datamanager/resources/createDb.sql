--  *********************************************************************
--  Update Database Script
--  *********************************************************************
--  Change Log: changeLogs/mychangelog.xml
--  Ran at: 5/8/13 4:45 PM
--  Against: uncc@localhost@jdbc:mysql://localhost:3306/oldDbSchema
--  Liquibase version: 2.0.5
--  *********************************************************************

--  Create Database Lock Table
CREATE TABLE `DATABASECHANGELOGLOCK` (`ID` INT NOT NULL, `LOCKED` TINYINT(1) NOT NULL, `LOCKGRANTED` DATETIME NULL, `LOCKEDBY` VARCHAR(255) NULL, CONSTRAINT `PK_DATABASECHANGELOGLOCK` PRIMARY KEY (`ID`));

INSERT INTO `DATABASECHANGELOGLOCK` (`ID`, `LOCKED`) VALUES (1, 0);

--  Lock Database
--  Create Database Change Log Table
CREATE TABLE `DATABASECHANGELOG` (`ID` VARCHAR(63) NOT NULL, `AUTHOR` VARCHAR(63) NOT NULL, `FILENAME` VARCHAR(200) NOT NULL, `DATEEXECUTED` DATETIME NOT NULL, `ORDEREXECUTED` INT NOT NULL, `EXECTYPE` VARCHAR(10) NOT NULL, `MD5SUM` VARCHAR(35) NULL, `DESCRIPTION` VARCHAR(255) NULL, `COMMENTS` VARCHAR(255) NULL, `TAG` VARCHAR(255) NULL, `LIQUIBASE` VARCHAR(20) NULL, CONSTRAINT `PK_DATABASECHANGELOG` PRIMARY KEY (`ID`, `AUTHOR`, `FILENAME`));

--  Changeset changeLogs/mychangelog.xml::1368045507728-1::lucy (generated)::(Checksum: 3:78597c4f1cb1aee9cdc8cc63a4cfa932)
CREATE TABLE `annotation_method` (`AnnotationMethodId` INT UNSIGNED NOT NULL, `MethodVersion` VARCHAR(255) NULL, `MethodCategory` VARCHAR(255) NULL, `MethodType` VARCHAR(255) NULL, `MethodSourceType` VARCHAR(255) NULL, `MethodName` VARCHAR(255) NULL, `MethodDescription` LONGTEXT NULL, `LoadDate` DATETIME NULL, `AquisitionDate` DATETIME NULL, `Obsolete` BIT NULL, CONSTRAINT `PK_ANNOTATION_METHOD` PRIMARY KEY (`AnnotationMethodId`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-1', '2.0.5', '3:78597c4f1cb1aee9cdc8cc63a4cfa932', 1);

--  Changeset changeLogs/mychangelog.xml::1368045507728-2::lucy (generated)::(Checksum: 3:b43a67363489ae3e33aae43786a3830e)
CREATE TABLE `assembled_unit` (`AssembledUnitId` INT UNSIGNED NOT NULL, `Organism` INT UNSIGNED NULL, `MolecularSequence` INT UNSIGNED NULL, `AssembledUnitName` VARCHAR(255) NULL, `AccessionVersion` VARCHAR(255) NULL, `SequenceLength` INT NULL, `AssembledUnitType` VARCHAR(127) NULL, `ReplicatingUnitType` VARCHAR(255) NULL, `RepUnitName` VARCHAR(255) NULL, `AssembledUnitLength` INT NULL, CONSTRAINT `PK_ASSEMBLED_UNIT` PRIMARY KEY (`AssembledUnitId`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-2', '2.0.5', '3:b43a67363489ae3e33aae43786a3830e', 2);

--  Changeset changeLogs/mychangelog.xml::1368045507728-3::lucy (generated)::(Checksum: 3:ffda9ae4d0fecaeb3c571def45759e6c)
CREATE TABLE `cluster_go_term` (`FeatureClusterId` INT UNSIGNED NOT NULL, `ClusterCategory` VARCHAR(255) NULL, `ClusterType` VARCHAR(255) NULL, `ClusterName` VARCHAR(255) NULL, `goName` VARCHAR(255) NULL, CONSTRAINT `PK_CLUSTER_GO_TERM` PRIMARY KEY (`FeatureClusterId`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-3', '2.0.5', '3:ffda9ae4d0fecaeb3c571def45759e6c', 3);

--  Changeset changeLogs/mychangelog.xml::1368045507728-4::lucy (generated)::(Checksum: 3:aed07b6f4ee4931ce710548ac11372c4)
CREATE TABLE `cluster_pathway` (`FeatureClusterId` INT UNSIGNED NOT NULL, `ClusterCategory` VARCHAR(255) NULL, `ClusterType` VARCHAR(255) NULL, `ClusterName` VARCHAR(255) NULL, `pathwayName` VARCHAR(255) NULL, CONSTRAINT `PK_CLUSTER_PATHWAY` PRIMARY KEY (`FeatureClusterId`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-4', '2.0.5', '3:aed07b6f4ee4931ce710548ac11372c4', 4);

--  Changeset changeLogs/mychangelog.xml::1368045507728-5::lucy (generated)::(Checksum: 3:7accbb1cdcb4f4c11fbc1823ee5d0a7e)
CREATE TABLE `fact_assembled_unit_aquisition` (`AssembledUnitAquisitionId` INT UNSIGNED NOT NULL, `Organism` INT NULL, `AssembledUnit` INT NULL, `AnnotationMethod` INT UNSIGNED NULL, `AssembledUnitName` VARCHAR(255) NULL, `MolecularSequence` INT UNSIGNED NULL, CONSTRAINT `PK_FACT_ASSEMBLED_UNIT_AQUISITION` PRIMARY KEY (`AssembledUnitAquisitionId`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-5', '2.0.5', '3:7accbb1cdcb4f4c11fbc1823ee5d0a7e', 5);

--  Changeset changeLogs/mychangelog.xml::1368045507728-6::lucy (generated)::(Checksum: 3:e3f5ee00d618c4a8c644d3409cc7bc04)
CREATE TABLE `fact_feature_go_anno` (`FactId` INT UNSIGNED NOT NULL, `Organism` INT NULL, `AssembledUnit` INT NULL, `Feature` INT NULL, `FeatureCluster` INT UNSIGNED NULL, `AnnotationMethod` INT UNSIGNED NULL, CONSTRAINT `PK_FACT_FEATURE_GO_ANNO` PRIMARY KEY (`FactId`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-6', '2.0.5', '3:e3f5ee00d618c4a8c644d3409cc7bc04', 6);

--  Changeset changeLogs/mychangelog.xml::1368045507728-7::lucy (generated)::(Checksum: 3:032422acd83e5f6ce0842369795ba058)
CREATE TABLE `fact_location_anno_fact` (`FactId` INT UNSIGNED NOT NULL, `Organism` INT NULL, `AssembledUnit` INT NULL, `Feature` INT NULL, `FeatureCluster` INT UNSIGNED NULL, `AnnotationMethod` INT UNSIGNED NULL, `Location` INT UNSIGNED NULL, `PrimaryName` VARCHAR(255) NULL, `FeatureType` VARCHAR(255) NULL, `Product` VARCHAR(255) NULL, CONSTRAINT `PK_FACT_LOCATION_ANNO_FACT` PRIMARY KEY (`FactId`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-7', '2.0.5', '3:032422acd83e5f6ce0842369795ba058', 7);

--  Changeset changeLogs/mychangelog.xml::1368045507728-8::lucy (generated)::(Checksum: 3:c0c8ab8e8c05ceed4da497ffbad7461f)
CREATE TABLE `fact_location_annofact_detail` (`FactId` INT UNSIGNED NOT NULL, `Organism` INT NULL, `AssembledUnit` INT NULL, `Feature` INT NULL, `FeatureCluster` INT UNSIGNED NULL, `AnnotationMethod` INT UNSIGNED NULL, `ParentFact` INT UNSIGNED NULL, `FeatureSequence` INT UNSIGNED NULL, `DetailType` VARCHAR(255) NULL, `DetailValue` LONGTEXT NULL, CONSTRAINT `PK_FACT_LOCATION_ANNOFACT_DETAIL` PRIMARY KEY (`FactId`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-8', '2.0.5', '3:c0c8ab8e8c05ceed4da497ffbad7461f', 8);

--  Changeset changeLogs/mychangelog.xml::1368045507728-9::lucy (generated)::(Checksum: 3:7eab9b49da778b0743d4fc49337eb619)
CREATE TABLE `fact_location_ortho_fact` (`FactId` INT UNSIGNED NOT NULL, `Organism` INT NULL, `AssembledUnit` INT NULL, `Feature` INT NULL, `FeatureCluster` INT UNSIGNED NULL, `AnnotationMethod` INT UNSIGNED NULL, `Location` INT UNSIGNED NULL, CONSTRAINT `PK_FACT_LOCATION_ORTHO_FACT` PRIMARY KEY (`FactId`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-9', '2.0.5', '3:7eab9b49da778b0743d4fc49337eb619', 9);

--  Changeset changeLogs/mychangelog.xml::1368045507728-10::lucy (generated)::(Checksum: 3:5a03544c0543e37b0abe69ba0e289223)
CREATE TABLE `fact_location_pathway_fact` (`FactId` INT UNSIGNED NOT NULL, `Organism` INT NULL, `AssembledUnit` INT NULL, `Feature` INT NULL, `FeatureCluster` INT UNSIGNED NULL, `AnnotationMethod` INT UNSIGNED NULL, `Location` INT UNSIGNED NULL, CONSTRAINT `PK_FACT_LOCATION_PATHWAY_FACT` PRIMARY KEY (`FactId`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-10', '2.0.5', '3:5a03544c0543e37b0abe69ba0e289223', 10);

--  Changeset changeLogs/mychangelog.xml::1368045507728-11::lucy (generated)::(Checksum: 3:ebb1034c05fc07320c53bf23e0e68860)
CREATE TABLE `feature` (`featureId` INT UNSIGNED NOT NULL, `Organism` INT NULL, `AssembledUnit` INT NULL, `PrimaryName` VARCHAR(255) NULL, `FeatureType` VARCHAR(255) NULL, `Product` VARCHAR(255) NULL, CONSTRAINT `PK_FEATURE` PRIMARY KEY (`featureId`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-11', '2.0.5', '3:ebb1034c05fc07320c53bf23e0e68860', 11);

--  Changeset changeLogs/mychangelog.xml::1368045507728-12::lucy (generated)::(Checksum: 3:8dec71e588e4e0bb292cb9e8585f8028)
CREATE TABLE `feature_cluster` (`FeatureClusterId` INT UNSIGNED NOT NULL, `ClusterCategory` VARCHAR(255) NULL, `ClusterType` VARCHAR(255) NULL, `ClusterName` VARCHAR(255) NULL, CONSTRAINT `PK_FEATURE_CLUSTER` PRIMARY KEY (`FeatureClusterId`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-12', '2.0.5', '3:8dec71e588e4e0bb292cb9e8585f8028', 12);

--  Changeset changeLogs/mychangelog.xml::1368045507728-13::lucy (generated)::(Checksum: 3:d539178dc8904d1a51b8d93e018bac35)
CREATE TABLE `feature_cluster_classification` (`ClusterClassificationId` INT UNSIGNED NOT NULL, `Organism` INT UNSIGNED NULL, `Feature` INT UNSIGNED NULL, `FeatureCluster` INT UNSIGNED NULL, `AnnotationMethod` INT UNSIGNED NULL, `AssembledUnit` INT UNSIGNED NULL, `TotalFeatureInClusterByMethod` INT UNSIGNED NULL, CONSTRAINT `PK_FEATURE_CLUSTER_CLASSIFICATION` PRIMARY KEY (`ClusterClassificationId`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-13', '2.0.5', '3:d539178dc8904d1a51b8d93e018bac35', 13);

--  Changeset changeLogs/mychangelog.xml::1368045507728-14::lucy (generated)::(Checksum: 3:78c8fe43ffbe5800ca5a916361489b68)
CREATE TABLE `feature_sequence` (`MolecularSequenceId` INT UNSIGNED NOT NULL, `SequenceType` VARCHAR(255) NULL, `ForwardSequence` LONGTEXT NULL, CONSTRAINT `PK_FEATURE_SEQUENCE` PRIMARY KEY (`MolecularSequenceId`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-14', '2.0.5', '3:78c8fe43ffbe5800ca5a916361489b68', 14);

--  Changeset changeLogs/mychangelog.xml::1368045507728-15::lucy (generated)::(Checksum: 3:c0cf1320fdfd45588ca4c53ddd48060a)
CREATE TABLE `graph_go` (`graphId` BIGINT NOT NULL, `Parent` INT NULL, `Child` INT NULL, `Level` INT NULL, `RelationType` VARCHAR(255) NULL, `AnnotationMethod` INT NULL, CONSTRAINT `PK_GRAPH_GO` PRIMARY KEY (`graphId`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-15', '2.0.5', '3:c0cf1320fdfd45588ca4c53ddd48060a', 15);

--  Changeset changeLogs/mychangelog.xml::1368045507728-16::lucy (generated)::(Checksum: 3:b7eee39483ae7657625cf3185546c83d)
CREATE TABLE `hibernate_sequences` (`sequence_name` VARCHAR(255) NULL, `sequence_next_hi_value` INT NULL);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-16', '2.0.5', '3:b7eee39483ae7657625cf3185546c83d', 16);

--  Changeset changeLogs/mychangelog.xml::1368045507728-17::lucy (generated)::(Checksum: 3:29150fcbf23287aa01646277622c2f87)
CREATE TABLE `location` (`LoctionId` INT UNSIGNED NOT NULL, `Organism` INT NULL, `AssembledUnit` INT NULL, `ProteinSequence` INT NULL, `AssembledSequence` INT NULL, `Feature` INT NULL, `MinPosition` INT UNSIGNED NULL, `MaxPosition` INT UNSIGNED NULL, `StartPosition` INT UNSIGNED NULL, `EndPosition` INT UNSIGNED NULL, `IsForward` BIT NULL, `GC_count` INT NULL, `NucleotideLength` INT UNSIGNED NULL, `GC_Percent` FLOAT NULL, `PrimaryName` VARCHAR(255) NULL, `FeatureType` VARCHAR(255) NULL, `Product` VARCHAR(255) NULL, CONSTRAINT `PK_LOCATION` PRIMARY KEY (`LoctionId`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-17', '2.0.5', '3:29150fcbf23287aa01646277622c2f87', 17);

--  Changeset changeLogs/mychangelog.xml::1368045507728-18::lucy (generated)::(Checksum: 3:4be19fb39bf051068931f1b52b49f907)
CREATE TABLE `molecular_sequence` (`MolecularSequenceId` INT UNSIGNED NOT NULL, `SequenceType` VARCHAR(255) NULL, `ForwardSequence` LONGTEXT NULL, CONSTRAINT `PK_MOLECULAR_SEQUENCE` PRIMARY KEY (`MolecularSequenceId`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-18', '2.0.5', '3:4be19fb39bf051068931f1b52b49f907', 18);

--  Changeset changeLogs/mychangelog.xml::1368045507728-19::lucy (generated)::(Checksum: 3:6de1962980950e679baf3a2dcc3ef2be)
CREATE TABLE `organism` (`OrganismId` INT UNSIGNED NOT NULL, `Kingdom` VARCHAR(255) NULL, `Phylum` VARCHAR(255) NULL, `TaxClass` VARCHAR(255) NULL, `TaxOrder` VARCHAR(255) NULL, `Family` VARCHAR(255) NULL, `Genus` VARCHAR(255) NULL, `Species` VARCHAR(255) NULL, `Strain` VARCHAR(255) NULL, `Sample` VARCHAR(255) NULL, `ShortName` VARCHAR(255) NULL, `TaxonomyIdentifier` INT NULL, `ProjectId` VARCHAR(255) NULL, CONSTRAINT `PK_ORGANISM` PRIMARY KEY (`OrganismId`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-19', '2.0.5', '3:6de1962980950e679baf3a2dcc3ef2be', 19);

--  Changeset changeLogs/mychangelog.xml::1368045507728-20::lucy (generated)::(Checksum: 3:aca11a934e9672c2c505c8d220311675)
ALTER TABLE `assembled_unit` ADD CONSTRAINT `FK144F48D136115FE2` FOREIGN KEY (`MolecularSequence`) REFERENCES `molecular_sequence` (`MolecularSequenceId`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-20', '2.0.5', '3:aca11a934e9672c2c505c8d220311675', 20);

--  Changeset changeLogs/mychangelog.xml::1368045507728-21::lucy (generated)::(Checksum: 3:3fd1ae5e0f3348fc3ec2ba452d958129)
ALTER TABLE `assembled_unit` ADD CONSTRAINT `FK144F48D1A0C765C4` FOREIGN KEY (`Organism`) REFERENCES `organism` (`OrganismId`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-21', '2.0.5', '3:3fd1ae5e0f3348fc3ec2ba452d958129', 21);

--  Changeset changeLogs/mychangelog.xml::1368045507728-22::lucy (generated)::(Checksum: 3:9ddd5d3da0c9e77248d713ee2b093bb8)
ALTER TABLE `fact_assembled_unit_aquisition` ADD CONSTRAINT `FKA19F42498C25E9E0` FOREIGN KEY (`AnnotationMethod`) REFERENCES `annotation_method` (`AnnotationMethodId`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-22', '2.0.5', '3:9ddd5d3da0c9e77248d713ee2b093bb8', 22);

--  Changeset changeLogs/mychangelog.xml::1368045507728-23::lucy (generated)::(Checksum: 3:ae973d81bdb7578ad2ebba457dab5bf4)
ALTER TABLE `fact_assembled_unit_aquisition` ADD CONSTRAINT `FKA19F424936115FE2` FOREIGN KEY (`MolecularSequence`) REFERENCES `molecular_sequence` (`MolecularSequenceId`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-23', '2.0.5', '3:ae973d81bdb7578ad2ebba457dab5bf4', 23);

--  Changeset changeLogs/mychangelog.xml::1368045507728-24::lucy (generated)::(Checksum: 3:3d5799c3b80f10023d5644e34d159a90)
ALTER TABLE `fact_feature_go_anno` ADD CONSTRAINT `FK30DDDF898C25E9E0` FOREIGN KEY (`AnnotationMethod`) REFERENCES `annotation_method` (`AnnotationMethodId`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-24', '2.0.5', '3:3d5799c3b80f10023d5644e34d159a90', 24);

--  Changeset changeLogs/mychangelog.xml::1368045507728-25::lucy (generated)::(Checksum: 3:89d8615cf8f044c6ee47015341acfe9e)
ALTER TABLE `fact_feature_go_anno` ADD CONSTRAINT `FK30DDDF89C84DDDD` FOREIGN KEY (`FeatureCluster`) REFERENCES `cluster_go_term` (`FeatureClusterId`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-25', '2.0.5', '3:89d8615cf8f044c6ee47015341acfe9e', 25);

--  Changeset changeLogs/mychangelog.xml::1368045507728-26::lucy (generated)::(Checksum: 3:961dac154e96649b7033bf85eed12a18)
ALTER TABLE `fact_location_anno_fact` ADD CONSTRAINT `FK501EA6068C25E9E0` FOREIGN KEY (`AnnotationMethod`) REFERENCES `annotation_method` (`AnnotationMethodId`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-26', '2.0.5', '3:961dac154e96649b7033bf85eed12a18', 26);

--  Changeset changeLogs/mychangelog.xml::1368045507728-27::lucy (generated)::(Checksum: 3:0be9b42b180094d436e69826e5cc2214)
ALTER TABLE `fact_location_anno_fact` ADD CONSTRAINT `FK501EA606D3385088` FOREIGN KEY (`FeatureCluster`) REFERENCES `feature_cluster` (`FeatureClusterId`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-27', '2.0.5', '3:0be9b42b180094d436e69826e5cc2214', 27);

--  Changeset changeLogs/mychangelog.xml::1368045507728-28::lucy (generated)::(Checksum: 3:78446e5a136df53293c96ce29a8c146c)
ALTER TABLE `fact_location_anno_fact` ADD CONSTRAINT `FK501EA606E679A38A` FOREIGN KEY (`Location`) REFERENCES `location` (`LoctionId`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-28', '2.0.5', '3:78446e5a136df53293c96ce29a8c146c', 28);

--  Changeset changeLogs/mychangelog.xml::1368045507728-29::lucy (generated)::(Checksum: 3:06f7d953cab1c3ed96aa3738b47dfb9c)
ALTER TABLE `fact_location_annofact_detail` ADD CONSTRAINT `FK7E454BFF8C25E9E0` FOREIGN KEY (`AnnotationMethod`) REFERENCES `annotation_method` (`AnnotationMethodId`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-29', '2.0.5', '3:06f7d953cab1c3ed96aa3738b47dfb9c', 29);

--  Changeset changeLogs/mychangelog.xml::1368045507728-30::lucy (generated)::(Checksum: 3:daa0ce94afcc6b7b8319925f93298a53)
ALTER TABLE `fact_location_annofact_detail` ADD CONSTRAINT `FK7E454BFFD3385088` FOREIGN KEY (`FeatureCluster`) REFERENCES `feature_cluster` (`FeatureClusterId`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-30', '2.0.5', '3:daa0ce94afcc6b7b8319925f93298a53', 30);

--  Changeset changeLogs/mychangelog.xml::1368045507728-31::lucy (generated)::(Checksum: 3:2f52ab0ad5c8017f938a10bde78f39e5)
ALTER TABLE `fact_location_annofact_detail` ADD CONSTRAINT `FK7E454BFFF56BD4FB` FOREIGN KEY (`FeatureSequence`) REFERENCES `feature_sequence` (`MolecularSequenceId`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-31', '2.0.5', '3:2f52ab0ad5c8017f938a10bde78f39e5', 31);

--  Changeset changeLogs/mychangelog.xml::1368045507728-32::lucy (generated)::(Checksum: 3:88f3bb330092d5328af7349ca36f1156)
ALTER TABLE `fact_location_annofact_detail` ADD CONSTRAINT `FK7E454BFFCE7EE470` FOREIGN KEY (`ParentFact`) REFERENCES `fact_location_anno_fact` (`FactId`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-32', '2.0.5', '3:88f3bb330092d5328af7349ca36f1156', 32);

--  Changeset changeLogs/mychangelog.xml::1368045507728-33::lucy (generated)::(Checksum: 3:e3eb3201c1a939a3caeb490e277aa246)
ALTER TABLE `fact_location_ortho_fact` ADD CONSTRAINT `FK78B4970A8C25E9E0` FOREIGN KEY (`AnnotationMethod`) REFERENCES `annotation_method` (`AnnotationMethodId`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-33', '2.0.5', '3:e3eb3201c1a939a3caeb490e277aa246', 33);

--  Changeset changeLogs/mychangelog.xml::1368045507728-34::lucy (generated)::(Checksum: 3:9e15584e28ec6cec6198f89ec9878b81)
ALTER TABLE `fact_location_ortho_fact` ADD CONSTRAINT `FK78B4970AD3385088` FOREIGN KEY (`FeatureCluster`) REFERENCES `feature_cluster` (`FeatureClusterId`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-34', '2.0.5', '3:9e15584e28ec6cec6198f89ec9878b81', 34);

--  Changeset changeLogs/mychangelog.xml::1368045507728-35::lucy (generated)::(Checksum: 3:73b58bb57f2283f06cb4332be3a91fce)
ALTER TABLE `fact_location_ortho_fact` ADD CONSTRAINT `FK78B4970AE679A38A` FOREIGN KEY (`Location`) REFERENCES `location` (`LoctionId`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-35', '2.0.5', '3:73b58bb57f2283f06cb4332be3a91fce', 35);

--  Changeset changeLogs/mychangelog.xml::1368045507728-36::lucy (generated)::(Checksum: 3:e7aaa8e1d70179866809afc7a5ec2c42)
ALTER TABLE `fact_location_pathway_fact` ADD CONSTRAINT `FKB93A7988C25E9E0` FOREIGN KEY (`AnnotationMethod`) REFERENCES `annotation_method` (`AnnotationMethodId`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-36', '2.0.5', '3:e7aaa8e1d70179866809afc7a5ec2c42', 36);

--  Changeset changeLogs/mychangelog.xml::1368045507728-37::lucy (generated)::(Checksum: 3:0e5a3fa1ae238cdc95924fb7e535af97)
ALTER TABLE `fact_location_pathway_fact` ADD CONSTRAINT `FKB93A7981FB533B4` FOREIGN KEY (`FeatureCluster`) REFERENCES `cluster_pathway` (`FeatureClusterId`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-37', '2.0.5', '3:0e5a3fa1ae238cdc95924fb7e535af97', 37);

--  Changeset changeLogs/mychangelog.xml::1368045507728-38::lucy (generated)::(Checksum: 3:de5ac8444146909561b348579c4cc94e)
ALTER TABLE `fact_location_pathway_fact` ADD CONSTRAINT `FKB93A798E679A38A` FOREIGN KEY (`Location`) REFERENCES `location` (`LoctionId`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-38', '2.0.5', '3:de5ac8444146909561b348579c4cc94e', 38);

--  Changeset changeLogs/mychangelog.xml::1368045507728-39::lucy (generated)::(Checksum: 3:5fbf61e9a4309dc435b372b26912fdba)
ALTER TABLE `feature_cluster_classification` ADD CONSTRAINT `FKD2258C348C25E9E0` FOREIGN KEY (`AnnotationMethod`) REFERENCES `annotation_method` (`AnnotationMethodId`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-39', '2.0.5', '3:5fbf61e9a4309dc435b372b26912fdba', 39);

--  Changeset changeLogs/mychangelog.xml::1368045507728-40::lucy (generated)::(Checksum: 3:5d289086671d63c6175b027dc7bb1edf)
ALTER TABLE `feature_cluster_classification` ADD CONSTRAINT `FKD2258C3430F3DEAC` FOREIGN KEY (`AssembledUnit`) REFERENCES `assembled_unit` (`AssembledUnitId`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-40', '2.0.5', '3:5d289086671d63c6175b027dc7bb1edf', 40);

--  Changeset changeLogs/mychangelog.xml::1368045507728-41::lucy (generated)::(Checksum: 3:49fa3977a4e249c946d8a1174ce7d1f1)
ALTER TABLE `feature_cluster_classification` ADD CONSTRAINT `FKD2258C34EE7D8DCC` FOREIGN KEY (`Feature`) REFERENCES `feature` (`featureId`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-41', '2.0.5', '3:49fa3977a4e249c946d8a1174ce7d1f1', 41);

--  Changeset changeLogs/mychangelog.xml::1368045507728-42::lucy (generated)::(Checksum: 3:6ad144a983e5ab5cd2836a8aa5cdcc4d)
ALTER TABLE `feature_cluster_classification` ADD CONSTRAINT `FKD2258C34D3385088` FOREIGN KEY (`FeatureCluster`) REFERENCES `feature_cluster` (`FeatureClusterId`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-42', '2.0.5', '3:6ad144a983e5ab5cd2836a8aa5cdcc4d', 42);

--  Changeset changeLogs/mychangelog.xml::1368045507728-43::lucy (generated)::(Checksum: 3:5a733cc0e392c24b8ebd5d5a2c9a7e5a)
ALTER TABLE `feature_cluster_classification` ADD CONSTRAINT `FKD2258C34A0C765C4` FOREIGN KEY (`Organism`) REFERENCES `organism` (`OrganismId`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-43', '2.0.5', '3:5a733cc0e392c24b8ebd5d5a2c9a7e5a', 43);

--  Changeset changeLogs/mychangelog.xml::1368045507728-44::lucy (generated)::(Checksum: 3:0623eeb03df77dfa70fd3ab1135ca4a9)
CREATE INDEX `AssembledUnitIndex` ON `fact_feature_go_anno`(`AssembledUnit`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-44', '2.0.5', '3:0623eeb03df77dfa70fd3ab1135ca4a9', 44);

--  Changeset changeLogs/mychangelog.xml::1368045507728-45::lucy (generated)::(Checksum: 3:f7c5dff2ba8551f38c74720ee1de666d)
CREATE INDEX `FeatureIndex` ON `fact_feature_go_anno`(`Feature`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-45', '2.0.5', '3:f7c5dff2ba8551f38c74720ee1de666d', 45);

--  Changeset changeLogs/mychangelog.xml::1368045507728-46::lucy (generated)::(Checksum: 3:23cde47e37242baaf83d159d59c7b5bb)
CREATE INDEX `OrganismIndex` ON `fact_feature_go_anno`(`Organism`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-46', '2.0.5', '3:23cde47e37242baaf83d159d59c7b5bb', 46);

--  Changeset changeLogs/mychangelog.xml::1368045507728-47::lucy (generated)::(Checksum: 3:100efaf5e1428bb007a73dad28f45511)
CREATE INDEX `AssembledUnitIndex` ON `fact_location_anno_fact`(`AssembledUnit`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-47', '2.0.5', '3:100efaf5e1428bb007a73dad28f45511', 47);

--  Changeset changeLogs/mychangelog.xml::1368045507728-48::lucy (generated)::(Checksum: 3:b8fe773f9d2cb78860bfc7992f9c27a8)
CREATE INDEX `FeatureIndex` ON `fact_location_anno_fact`(`Feature`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-48', '2.0.5', '3:b8fe773f9d2cb78860bfc7992f9c27a8', 48);

--  Changeset changeLogs/mychangelog.xml::1368045507728-49::lucy (generated)::(Checksum: 3:fc0537d070e04776a54ee0c7921db14d)
CREATE INDEX `OrganismIndex` ON `fact_location_anno_fact`(`Organism`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-49', '2.0.5', '3:fc0537d070e04776a54ee0c7921db14d', 49);

--  Changeset changeLogs/mychangelog.xml::1368045507728-50::lucy (generated)::(Checksum: 3:68f5ae7f33c91133335d4b270661598e)
CREATE INDEX `AssembledUnitIndex` ON `fact_location_annofact_detail`(`AssembledUnit`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-50', '2.0.5', '3:68f5ae7f33c91133335d4b270661598e', 50);

--  Changeset changeLogs/mychangelog.xml::1368045507728-51::lucy (generated)::(Checksum: 3:e940db44c12ad3ea57427594ac2210c6)
CREATE INDEX `FeatureIndex` ON `fact_location_annofact_detail`(`Feature`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-51', '2.0.5', '3:e940db44c12ad3ea57427594ac2210c6', 51);

--  Changeset changeLogs/mychangelog.xml::1368045507728-52::lucy (generated)::(Checksum: 3:431108ca3cf49a779b02525be3df8786)
CREATE INDEX `OrganismIndex` ON `fact_location_annofact_detail`(`Organism`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-52', '2.0.5', '3:431108ca3cf49a779b02525be3df8786', 52);

--  Changeset changeLogs/mychangelog.xml::1368045507728-53::lucy (generated)::(Checksum: 3:16a1361cffa503ea9876026e2bd09ed7)
CREATE INDEX `AssembledUnitIndex` ON `fact_location_ortho_fact`(`AssembledUnit`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-53', '2.0.5', '3:16a1361cffa503ea9876026e2bd09ed7', 53);

--  Changeset changeLogs/mychangelog.xml::1368045507728-54::lucy (generated)::(Checksum: 3:cca64f42869951ea6ab2784fa1168c93)
CREATE INDEX `FeatureIndex` ON `fact_location_ortho_fact`(`Feature`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-54', '2.0.5', '3:cca64f42869951ea6ab2784fa1168c93', 54);

--  Changeset changeLogs/mychangelog.xml::1368045507728-55::lucy (generated)::(Checksum: 3:8161d5a9e2f4f80bab6bf750a8a32fe3)
CREATE INDEX `OrganismIndex` ON `fact_location_ortho_fact`(`Organism`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-55', '2.0.5', '3:8161d5a9e2f4f80bab6bf750a8a32fe3', 55);

--  Changeset changeLogs/mychangelog.xml::1368045507728-56::lucy (generated)::(Checksum: 3:b463dc64edd0c76936bfaed429a8499e)
CREATE INDEX `AssembledUnitIndex` ON `fact_location_pathway_fact`(`AssembledUnit`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-56', '2.0.5', '3:b463dc64edd0c76936bfaed429a8499e', 56);

--  Changeset changeLogs/mychangelog.xml::1368045507728-57::lucy (generated)::(Checksum: 3:c24a0d172f9098d438d58df4ad427c10)
CREATE INDEX `FeatureIndex` ON `fact_location_pathway_fact`(`Feature`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-57', '2.0.5', '3:c24a0d172f9098d438d58df4ad427c10', 57);

--  Changeset changeLogs/mychangelog.xml::1368045507728-58::lucy (generated)::(Checksum: 3:345388e87e8a5fe66cd049d10832b948)
CREATE INDEX `OrganismIndex` ON `fact_location_pathway_fact`(`Organism`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-58', '2.0.5', '3:345388e87e8a5fe66cd049d10832b948', 58);

--  Changeset changeLogs/mychangelog.xml::1368045507728-59::lucy (generated)::(Checksum: 3:7a3276b11db2c6642262e623f4ba2706)
CREATE INDEX `AssembledUnitIndex` ON `feature`(`AssembledUnit`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-59', '2.0.5', '3:7a3276b11db2c6642262e623f4ba2706', 59);

--  Changeset changeLogs/mychangelog.xml::1368045507728-60::lucy (generated)::(Checksum: 3:6589beb290fad0c34169a19d1aac94ac)
CREATE INDEX `OrganismIndex` ON `feature`(`Organism`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-60', '2.0.5', '3:6589beb290fad0c34169a19d1aac94ac', 60);

--  Changeset changeLogs/mychangelog.xml::1368045507728-61::lucy (generated)::(Checksum: 3:2b6bb18e7941ae8e26401c4d61922113)
CREATE INDEX `AnnotationMethodIndex` ON `graph_go`(`AnnotationMethod`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-61', '2.0.5', '3:2b6bb18e7941ae8e26401c4d61922113', 61);

--  Changeset changeLogs/mychangelog.xml::1368045507728-62::lucy (generated)::(Checksum: 3:55764f269a3fd1de309b1772452a545e)
CREATE INDEX `ChildIndex` ON `graph_go`(`Child`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-62', '2.0.5', '3:55764f269a3fd1de309b1772452a545e', 62);

--  Changeset changeLogs/mychangelog.xml::1368045507728-63::lucy (generated)::(Checksum: 3:4576035494fb842853883755ec26ce9a)
CREATE INDEX `ParentIndex` ON `graph_go`(`Parent`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-63', '2.0.5', '3:4576035494fb842853883755ec26ce9a', 63);

--  Changeset changeLogs/mychangelog.xml::1368045507728-64::lucy (generated)::(Checksum: 3:e015670609102b9ab3d32fdaccc6ad90)
CREATE INDEX `AssembledUnitIndex` ON `location`(`AssembledUnit`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-64', '2.0.5', '3:e015670609102b9ab3d32fdaccc6ad90', 64);

--  Changeset changeLogs/mychangelog.xml::1368045507728-65::lucy (generated)::(Checksum: 3:eb70b8f2b71d05ce5719947deee6090c)
CREATE INDEX `FeatureIndex` ON `location`(`Feature`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-65', '2.0.5', '3:eb70b8f2b71d05ce5719947deee6090c', 65);

--  Changeset changeLogs/mychangelog.xml::1368045507728-66::lucy (generated)::(Checksum: 3:107ca4d15b2fd43bf10f7a9339ebf89b)
CREATE INDEX `OrganismIndex` ON `location`(`Organism`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-66', '2.0.5', '3:107ca4d15b2fd43bf10f7a9339ebf89b', 66);

--  Changeset changeLogs/mychangelog.xml::1368045507728-67::lucy (generated)::(Checksum: 3:7e905b8912642f2ba3ed31c1bfa64d1b)
CREATE INDEX `organism_projectId_index` ON `organism`(`ProjectId`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-67', '2.0.5', '3:7e905b8912642f2ba3ed31c1bfa64d1b', 67);

--  Changeset changeLogs/mychangelog.xml::1368045507728-68::lucy (generated)::(Checksum: 3:1e826f3d36394cefe1650ddc0f868e3a)
CREATE INDEX `organism_taxonomyIdentifier_index` ON `organism`(`TaxonomyIdentifier`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('lucy (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'changeLogs/mychangelog.xml', '1368045507728-68', '2.0.5', '3:1e826f3d36394cefe1650ddc0f868e3a', 68);

