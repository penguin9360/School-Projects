/**
 * @file
 * Contains the bmp class and supporting structures and classes.
 *
 * This file describes the interface that your code should use to process
 * BMP files. The actual implementation can be found in bmp.cpp, but you
 * do not need to refer to it to complete the lab.
 *
 * @see example.cpp for code making use of this class.
 */

#ifndef BMP_H
#define BMP_H

#ifdef _WIN32
#define PACKED __attribute__((gcc_struct, packed))
#else
#define PACKED __attribute__((packed))
#endif

#include <fstream>
#include <stdexcept>
#include <string>
#include <vector>

#include <stdint.h>

/**
 * A single pixel in an image.
 */
struct pixel {
    // defined in the same order as 24 bpp BMPs
    uint8_t blue;
    uint8_t green;
    uint8_t red;
};

/**
 * A BMP image.
 *
 * This class is only capable of handling uncompressed, 24 bpp BMPs.
 */
class bmp {
  public:
    /**
		 * Constructs a BMP object with the specified width and height.
		 * The image is initialized to white.
		 *
		 * @param width  The desired width.
		 * @param height The desired height.
		 * @throws invalid_argument on negative width or height.
		 */
    bmp(int32_t width, int32_t height);

    /**
		 * Constructs a BMP object from the specified file.
		 *
		 * @param file_name The file to read in.
		 * @throws invalid_argument if file cannot be read, file is invalid
		 *                          or file format is not supported.
		 */
    explicit bmp(const std::string &file_name);

    /**
		 * Reads a BMP image from the specified file.
		 *
		 * @param file_name The file to read in.
		 * @throws invalid_argument if file cannot be read, file is invalid
		 *                          or file format is not supported.
		 */
    void read_from_file(const std::string &file_name);

    /**
		 * Writes a BMP image to the specified file.
		 *
		 * @param file_name The file to write to.
		 * @throws invalid_argument if file cannot be opened for writing.
		 * @throws ofstream::failure if a write fails
		 */
    void write_to_file(const std::string &file_name);

    /**
		 * Returns a pointer to the pixel at coordinates (x, y) which
		 * can be used to change the pixel. (0, 0) is the top left corner.
		 *
		 * @param x The x-coordinate of the pixel
		 * @param y The y-coordinate of the pixel.
		 * @return A pointer to the pixel at those coordinates.
		 * @throws out_of_range if coordinate is out of bounds.
		 */
    pixel *operator()(int32_t x, int32_t y);

    /**
		 * Returns a pointer to the pixel at coordinates (x, y) which
		 * cannot be used to change the pixel. (0, 0) is the top left corner.
		 *
		 * @param x The x-coordinate of the pixel
		 * @param y The y-coordinate of the pixel.
		 * @return A pointer to the pixel at those coordinates.
		 * @throws out_of_range if coordinate is out of bounds.
		 */
    const pixel *operator()(int32_t x, int32_t y) const;

    /**
		 * Gets the width of the image.
		 */
    int32_t width() const;

    /**
		 * Gets the height of the image.
		 */
    int32_t height() const;

  private:
    struct PACKED bmp_header {
        char identifier[2];
        uint32_t file_size;
        uint32_t reserved;
        uint32_t data_offset;

        // initializes the header
        void init(uint32_t data_size);

        // reads the header from a file
        void read_from_file(std::ifstream &file);

        // writes the header to a file
        void write_to_file(std::ofstream &file);

      private:
        const static char *identifier_;
    } file_header_;

    struct PACKED bitmap_info_header {
        uint32_t header_size;
        int32_t width;
        int32_t height;
        uint16_t num_planes;
        uint16_t bpp;
        uint32_t compression;
        uint32_t data_size;
        int32_t x_ppm;
        int32_t y_ppm;
        uint32_t num_colors;
        uint32_t num_important_colors;

        // initializes the header
        void init(int32_t width, int32_t height);

        // reads the header from a file
        void read_from_file(std::ifstream &file);

        // writes the header to a file
        void write_to_file(std::ofstream &file);

        // computes the number of padding bytes needed per row
        uint32_t compute_padding();

      private:
        enum compression_method {
            BI_RGB,
            BI_RLE8,
            BI_RLE4,
            BI_BITFIELDS,
            BI_JPEG,
            BI_PNG,
            BI_ALPHABITFIELDS
        };

        const static int32_t default_windows_ppm_ = 3780; // 96 dpi
        const static uint16_t true_color_bpp_ = 24;
    } dib_header_;

    // the actual pixels of the image
    std::vector<pixel> pixels_;

    // reads the pixels from a file
    void read_pixels_(std::ifstream &file);

    // writes the pixels to a file
    void write_pixels_(std::ofstream &file);
};

// expose one-liners for inlining
inline bmp::bmp(const std::string &file_name) {
    read_from_file(file_name);
}

inline pixel *
bmp::operator()(int32_t x, int32_t y) {
    return const_cast<pixel *>((*const_cast<const bmp *>(this))(x, y));
}

inline int32_t
bmp::width() const {
    return dib_header_.width;
}

inline int32_t
bmp::height() const {
    return dib_header_.height;
}

inline void
bmp::bmp_header::write_to_file(std::ofstream &file) {
    file.write(reinterpret_cast<char *>(this), sizeof(bmp_header));
}

inline void
bmp::bitmap_info_header::write_to_file(std::ofstream &file) {
    file.write(reinterpret_cast<char *>(this), sizeof(bitmap_info_header));
}

inline uint32_t
bmp::bitmap_info_header::compute_padding() {
    return -(width * sizeof(pixel)) & 3;
}

#endif
