import cv2
import numpy as np


detector = cv2.xfeatures2d.SURF_create()

queries = {}

query_set = ["ancient-lamp-600.jpg",    "magic-lamp-600.jpg",  "mayan-calendar-600.jpg",  "one-ring-600.jpg",    "roman-glass-600.jpg",
"audio-cassette-600.jpg",  "mammoth-600.jpg",     "mjolnir-hammer-600.jpg",  "pueblo-pot-600.jpg",  "willow-plate-600.jpg"]

for q in query_set:
	queries[q] = {}
	t = cv2.imread("query-image/" + q, cv2.IMREAD_GRAYSCALE)
	queries[q]['k'], queries[q]['d'] = detector.detectAndCompute(img, None) 

img = cv2.imread("query-image/irobot_scene_1.jpg", cv2.IMREAD_GRAYSCALE)


# Initiate SIFT detector

for q in query_set:
	# find the keypoints and descriptors with SIFT
	kp2, des2 = sift.detectAndCompute(img2,None)

	# BFMatcher with default params
	bf = cv2.BFMatcher()
	matches = bf.knnMatch(queries[q]['d'],des2, k=2)

	# Apply ratio test
	good = []
	for m,n in matches:
		if m.distance < 0.75*n.distance:
			good.append([m])

	# cv2.drawMatchesKnn expects list of lists as matches.
	img3 = cv2.drawMatchesKnn(img1,kp1,img2,kp2,good,flags=2)

	plt.imshow(img3),plt.show()
