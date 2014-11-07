CREATE TABLE `DATABASECHANGELOG` (`ID` VARCHAR(63) NOT NULL, `AUTHOR` VARCHAR(63) NOT NULL, `FILENAME` VARCHAR(200) NOT NULL, `DATEEXECUTED` DATETIME NOT NULL, `ORDEREXECUTED` INT NOT NULL, `EXECTYPE` VARCHAR(10) NOT NULL, `MD5SUM` VARCHAR(35) NULL, `DESCRIPTION` VARCHAR(255) NULL, `COMMENTS` VARCHAR(255) NULL, `TAG` VARCHAR(255) NULL, `LIQUIBASE` VARCHAR(20) NULL, CONSTRAINT `PK_DATABASECHANGELOG` PRIMARY KEY (`ID`, `AUTHOR`, `FILENAME`));

INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-1', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:21', 1, 'EXECUTED', '3:78597c4f1cb1aee9cdc8cc63a4cfa932', 'Create Table', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-10', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:22', 10, 'EXECUTED', '3:5a03544c0543e37b0abe69ba0e289223', 'Create Table', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-11', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:22', 11, 'EXECUTED', '3:ebb1034c05fc07320c53bf23e0e68860', 'Create Table', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-12', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:22', 12, 'EXECUTED', '3:8dec71e588e4e0bb292cb9e8585f8028', 'Create Table', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-13', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:22', 13, 'EXECUTED', '3:d539178dc8904d1a51b8d93e018bac35', 'Create Table', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-14', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:22', 14, 'EXECUTED', '3:78c8fe43ffbe5800ca5a916361489b68', 'Create Table', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-15', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:22', 15, 'EXECUTED', '3:c0cf1320fdfd45588ca4c53ddd48060a', 'Create Table', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-16', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:22', 16, 'EXECUTED', '3:b7eee39483ae7657625cf3185546c83d', 'Create Table', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-17', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:23', 17, 'EXECUTED', '3:29150fcbf23287aa01646277622c2f87', 'Create Table', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-18', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:23', 18, 'EXECUTED', '3:4be19fb39bf051068931f1b52b49f907', 'Create Table', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-19', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:23', 19, 'EXECUTED', '3:6de1962980950e679baf3a2dcc3ef2be', 'Create Table', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-2', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:21', 2, 'EXECUTED', '3:b43a67363489ae3e33aae43786a3830e', 'Create Table', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-20', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:23', 20, 'EXECUTED', '3:aca11a934e9672c2c505c8d220311675', 'Add Foreign Key Constraint', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-21', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:23', 21, 'EXECUTED', '3:3fd1ae5e0f3348fc3ec2ba452d958129', 'Add Foreign Key Constraint', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-22', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:24', 22, 'EXECUTED', '3:9ddd5d3da0c9e77248d713ee2b093bb8', 'Add Foreign Key Constraint', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-23', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:24', 23, 'EXECUTED', '3:ae973d81bdb7578ad2ebba457dab5bf4', 'Add Foreign Key Constraint', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-24', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:24', 24, 'EXECUTED', '3:3d5799c3b80f10023d5644e34d159a90', 'Add Foreign Key Constraint', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-25', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:25', 25, 'EXECUTED', '3:89d8615cf8f044c6ee47015341acfe9e', 'Add Foreign Key Constraint', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-26', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:25', 26, 'EXECUTED', '3:961dac154e96649b7033bf85eed12a18', 'Add Foreign Key Constraint', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-27', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:25', 27, 'EXECUTED', '3:0be9b42b180094d436e69826e5cc2214', 'Add Foreign Key Constraint', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-28', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:25', 28, 'EXECUTED', '3:78446e5a136df53293c96ce29a8c146c', 'Add Foreign Key Constraint', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-29', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:26', 29, 'EXECUTED', '3:06f7d953cab1c3ed96aa3738b47dfb9c', 'Add Foreign Key Constraint', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-3', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:21', 3, 'EXECUTED', '3:ffda9ae4d0fecaeb3c571def45759e6c', 'Create Table', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-30', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:27', 30, 'EXECUTED', '3:daa0ce94afcc6b7b8319925f93298a53', 'Add Foreign Key Constraint', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-31', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:27', 31, 'EXECUTED', '3:2f52ab0ad5c8017f938a10bde78f39e5', 'Add Foreign Key Constraint', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-32', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:27', 32, 'EXECUTED', '3:88f3bb330092d5328af7349ca36f1156', 'Add Foreign Key Constraint', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-33', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:28', 33, 'EXECUTED', '3:e3eb3201c1a939a3caeb490e277aa246', 'Add Foreign Key Constraint', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-34', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:28', 34, 'EXECUTED', '3:9e15584e28ec6cec6198f89ec9878b81', 'Add Foreign Key Constraint', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-35', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:28', 35, 'EXECUTED', '3:73b58bb57f2283f06cb4332be3a91fce', 'Add Foreign Key Constraint', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-36', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:28', 36, 'EXECUTED', '3:e7aaa8e1d70179866809afc7a5ec2c42', 'Add Foreign Key Constraint', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-37', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:29', 37, 'EXECUTED', '3:0e5a3fa1ae238cdc95924fb7e535af97', 'Add Foreign Key Constraint', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-38', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:29', 38, 'EXECUTED', '3:de5ac8444146909561b348579c4cc94e', 'Add Foreign Key Constraint', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-39', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:29', 39, 'EXECUTED', '3:5fbf61e9a4309dc435b372b26912fdba', 'Add Foreign Key Constraint', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-4', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:21', 4, 'EXECUTED', '3:aed07b6f4ee4931ce710548ac11372c4', 'Create Table', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-40', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:29', 40, 'EXECUTED', '3:5d289086671d63c6175b027dc7bb1edf', 'Add Foreign Key Constraint', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-41', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:30', 41, 'EXECUTED', '3:49fa3977a4e249c946d8a1174ce7d1f1', 'Add Foreign Key Constraint', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-42', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:30', 42, 'EXECUTED', '3:6ad144a983e5ab5cd2836a8aa5cdcc4d', 'Add Foreign Key Constraint', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-43', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:30', 43, 'EXECUTED', '3:5a733cc0e392c24b8ebd5d5a2c9a7e5a', 'Add Foreign Key Constraint', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-44', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:30', 44, 'EXECUTED', '3:0623eeb03df77dfa70fd3ab1135ca4a9', 'Create Index', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-45', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:30', 45, 'EXECUTED', '3:f7c5dff2ba8551f38c74720ee1de666d', 'Create Index', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-46', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:31', 46, 'EXECUTED', '3:23cde47e37242baaf83d159d59c7b5bb', 'Create Index', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-47', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:31', 47, 'EXECUTED', '3:100efaf5e1428bb007a73dad28f45511', 'Create Index', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-48', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:31', 48, 'EXECUTED', '3:b8fe773f9d2cb78860bfc7992f9c27a8', 'Create Index', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-49', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:31', 49, 'EXECUTED', '3:fc0537d070e04776a54ee0c7921db14d', 'Create Index', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-5', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:21', 5, 'EXECUTED', '3:7accbb1cdcb4f4c11fbc1823ee5d0a7e', 'Create Table', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-50', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:31', 50, 'EXECUTED', '3:68f5ae7f33c91133335d4b270661598e', 'Create Index', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-51', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:31', 51, 'EXECUTED', '3:e940db44c12ad3ea57427594ac2210c6', 'Create Index', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-52', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:32', 52, 'EXECUTED', '3:431108ca3cf49a779b02525be3df8786', 'Create Index', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-53', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:32', 53, 'EXECUTED', '3:16a1361cffa503ea9876026e2bd09ed7', 'Create Index', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-54', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:32', 54, 'EXECUTED', '3:cca64f42869951ea6ab2784fa1168c93', 'Create Index', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-55', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:32', 55, 'EXECUTED', '3:8161d5a9e2f4f80bab6bf750a8a32fe3', 'Create Index', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-56', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:32', 56, 'EXECUTED', '3:b463dc64edd0c76936bfaed429a8499e', 'Create Index', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-57', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:32', 57, 'EXECUTED', '3:c24a0d172f9098d438d58df4ad427c10', 'Create Index', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-58', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:33', 58, 'EXECUTED', '3:345388e87e8a5fe66cd049d10832b948', 'Create Index', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-59', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:33', 59, 'EXECUTED', '3:7a3276b11db2c6642262e623f4ba2706', 'Create Index', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-6', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:21', 6, 'EXECUTED', '3:e3f5ee00d618c4a8c644d3409cc7bc04', 'Create Table', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-60', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:33', 60, 'EXECUTED', '3:6589beb290fad0c34169a19d1aac94ac', 'Create Index', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-61', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:33', 61, 'EXECUTED', '3:2b6bb18e7941ae8e26401c4d61922113', 'Create Index', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-62', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:33', 62, 'EXECUTED', '3:55764f269a3fd1de309b1772452a545e', 'Create Index', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-63', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:34', 63, 'EXECUTED', '3:4576035494fb842853883755ec26ce9a', 'Create Index', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-64', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:34', 64, 'EXECUTED', '3:e015670609102b9ab3d32fdaccc6ad90', 'Create Index', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-65', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:34', 65, 'EXECUTED', '3:eb70b8f2b71d05ce5719947deee6090c', 'Create Index', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-66', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:34', 66, 'EXECUTED', '3:107ca4d15b2fd43bf10f7a9339ebf89b', 'Create Index', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-67', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:34', 67, 'EXECUTED', '3:7e905b8912642f2ba3ed31c1bfa64d1b', 'Create Index', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-68', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:34', 68, 'EXECUTED', '3:1e826f3d36394cefe1650ddc0f868e3a', 'Create Index', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-7', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:21', 7, 'EXECUTED', '3:032422acd83e5f6ce0842369795ba058', 'Create Table', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-8', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:21', 8, 'EXECUTED', '3:c0c8ab8e8c05ceed4da497ffbad7461f', 'Create Table', '', NULL, '2.0.5');
INSERT INTO DATABASECHANGELOG VALUES ('1368045507728-9', 'lucy (generated)', 'edu/uncc/genosets/datamanager/resources/db.changelog-1.0.xml', '2013-05-14 14:33:22', 9, 'EXECUTED', '3:7eab9b49da778b0743d4fc49337eb619', 'Create Table', '', NULL, '2.0.5');


