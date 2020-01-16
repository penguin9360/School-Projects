INSERT INTO patients (MID, firstName,lastName,zip)
VALUES (64, 'f1', 'l1', '61820');

-- INSERT INTO users(MID, password, role, sQuestion, sAnswer)
-- 			VALUES (64, '30c952fab122c3f9759f02a6d95c3758b246b4fee239957b2d4fee46e26170c4', 'patient', 'how you doin?', 'good')
--  ON DUPLICATE KEY UPDATE MID = MID;
 /*password: pw*/


INSERT INTO allergies(PatientID,Code,Description, FirstFound)
	VALUES (64, '', 'Smog', '2007-06-05 20:33:58'),
	    (64, '', 'Pollen', '2007-06-05 20:33:57');