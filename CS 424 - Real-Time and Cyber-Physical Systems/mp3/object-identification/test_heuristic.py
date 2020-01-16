import sys
import glob
import cv2
import numpy as np
import matplotlib.pyplot as plt

WHITE_LOW_TOL = 0.6
WHITE_HIGH_TOL = 0.88
TOTAL_PIXELS = 960 * 1280
PIXEL_THRESH = 0.125 * TOTAL_PIXELS
DEBUG = False 


def hasObject(img):
    mask = np.logical_and(img > WHITE_LOW_TOL, img < WHITE_HIGH_TOL)
    num_white = np.sum(mask)
    if DEBUG:
        fig, axes = plt.subplots(2, 1)
        axes[0].imshow(img)
        axes[1].imshow(mask)
        plt.show()
    return num_white > PIXEL_THRESH

def main(dir_path):
    if dir_path[-1] != "/":
        dir_path += "/"

    print(f"THRESHOLD {PIXEL_THRESH}")
    img_paths = [f for f in glob.glob(dir_path + "*.jpg")]
    print(f"Found {len(img_paths)} imgs")
    imgs = [cv2.imread(img, cv2.IMREAD_GRAYSCALE) for img in img_paths]
    imgs = [img / 255 for img in imgs if img is not None]
    print(f"Loaded {len(imgs)} imgs")
    print(imgs[0].shape)
    filtered_out = 0
    for img in imgs:
        if not hasObject(img):
            filtered_out += 1
            plt.imshow(img)
            plt.show()
    print(f"Filtered out {filtered_out} imgs")


if __name__ == "__main__":
    if len(sys.argv) != 2:
        raise ValueError(f"python3 test_heuristic.py {path_to_example_imgs}")
    main(sys.argv[1])
