INSERT INTO patients
(MID,
firstName,
lastName,
email,
address1,
address2,
city,
state,
zip,
phone,
eName,
ePhone,
iCName,
iCAddress1,
iCAddress2,
iCCity,
ICState,
iCZip,
iCPhone,
iCID,
DateOfBirth,
DateOfDeath,
CauseOfDeath,
MotherMID,
FatherMID,
BloodType,
Ethnicity,
Gender,
TopicalNotes
)
VALUES (
61,
'Al',
'Pat',
'',
'344 Bob Street',
'',
'Raleigh',
'NC',
'27608',
'555-555-5555',
'Mr Emergency',
'555-555-5551',
'IC',
'Street1',
'Street2',
'City',
'PA',
'19003-2715',
'555-555-5555',
'1',
'1984-05-19',
'2005-03-10',
'250.10',
1,
0,
'O-',
'Caucasian',
'Male',
''
)  ON DUPLICATE KEY UPDATE MID = MID;

INSERT INTO users(MID, password, role, sQuestion, sAnswer)
			VALUES (61, '30c952fab122c3f9759f02a6d95c3758b246b4fee239957b2d4fee46e26170c4', 'patient', 'how you doin?', 'good')
 ON DUPLICATE KEY UPDATE MID = MID;
 /*password: pw*/

DELETE FROM allergies WHERE PatientID = 61;
INSERT INTO allergies(PatientID,Code,Description, FirstFound)
	VALUES (61, '', 'Pollen', '2007-06-05 20:33:58');