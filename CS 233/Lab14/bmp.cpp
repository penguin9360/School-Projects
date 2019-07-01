/**
 * @file
 * Contains the implementation of the bmp class.
 */

#include "bmp.h"
#include <sstream>

using std::ifstream;
using std::invalid_argument; // your argument is invalid
using std::ofstream;
using std::string;

bmp::bmp(int32_t width, int32_t height) {
    if (width < 0 || height < 0) {
        throw invalid_argument("width and height cannot be negative");
    }

    dib_header_.init(width, height);
    file_header_.init(dib_header_.data_size);
    pixel white = {255, 255, 255};
    pixels_.resize(width * height, white);
}

void
bmp::read_from_file(const string &file_name) {
    ifstream file(file_name.c_str(), ifstream::binary);
    if (!file.is_open()) {
        // FYI, it's good practice to create separate exception classes for
        // different sorts of exceptions instead of stuffing information into
        // the exception error message, but if I were to do that, I would really
        // want to create a namespace to hold all the bmp-related classes, and
        // I don't want to get into that
        throw invalid_argument("could not read from file " + file_name);
    }

    file.exceptions(ifstream::badbit | ifstream::failbit | ifstream::eofbit);
    try {
        file_header_.read_from_file(file);
        dib_header_.read_from_file(file);
        read_pixels_(file);
    } catch (const ifstream::failure &) {
        throw invalid_argument(file_name + " is not a valid BMP file");
    } catch (const invalid_argument &) {
        // the called functions don't know the file name so they can't include
        // that in the exception message, and C ++ exceptions don't have any
        // stack trace information I would be losing by doing this
        throw invalid_argument(file_name + " is of an unsupported format");
    }
}

void
bmp::write_to_file(const string &file_name) {
    ofstream file(file_name.c_str(), ofstream::binary | ofstream::trunc);
    if (!file.is_open()) {
        throw invalid_argument("could not write to file " + file_name);
    }

    file.exceptions(ofstream::badbit | ofstream::failbit | ofstream::eofbit);
    file_header_.write_to_file(file);
    dib_header_.write_to_file(file);
    write_pixels_(file);
}

const pixel *
bmp::operator()(int32_t x, int32_t y) const {
    if (x < 0 || x >= width() || y < 0 || y >= height()) {
        std::ostringstream ss;
        ss << "tried to access coordinate (" << x << "," << y << ")"
                                                                 "on image with dimensions ("
           << width() << "," << height() << ")";
        throw std::out_of_range(ss.str());
    }

    return &pixels_[y * width() + x];
}

const char *bmp::bmp_header::identifier_ = "BM";

void
bmp::bmp_header::init(uint32_t data_size) {
    identifier[0] = identifier_[0];
    identifier[1] = identifier_[1];
    file_size = data_size + sizeof(bmp_header);
    reserved = 0;
    data_offset = sizeof(bmp_header) + sizeof(bitmap_info_header);
}

void
bmp::bmp_header::read_from_file(ifstream &file) {
    file.read(reinterpret_cast<char *>(this), sizeof(bmp_header));
    if (identifier[0] != identifier_[0] || identifier[1] != identifier_[1]) {
        throw invalid_argument("unsupported file format");
    }
}

void
bmp::bitmap_info_header::init(int32_t width, int32_t height) {
    header_size = sizeof(bitmap_info_header);
    this->width = width;
    this->height = height;
    num_planes = 1;
    bpp = true_color_bpp_;
    compression = BI_RGB;
    data_size = height * (width * sizeof(pixel) + compute_padding());
    x_ppm = default_windows_ppm_;
    y_ppm = default_windows_ppm_;
    num_colors = 0;
    num_important_colors = 0;
}

void
bmp::bitmap_info_header::read_from_file(ifstream &file) {
    file.read(reinterpret_cast<char *>(this), sizeof(bitmap_info_header));
    if (header_size != sizeof(bitmap_info_header) || bpp != true_color_bpp_ ||
        compression != BI_RGB) {
        throw invalid_argument("unsupported file format");
    }
}

void
bmp::read_pixels_(ifstream &file) {
    pixels_.resize(width() * height());
    uint32_t padding = dib_header_.compute_padding();
    int32_t row, row_increment;
    if (height() >= 0) {
        // rows stored bottom to top
        row = height() - 1;
        row_increment = -1;
    } else {
        // rows stored top to bottom
        row = 0;
        row_increment = 1;
        dib_header_.height = -height();
    }

    for (int32_t read_rows = 0; read_rows < height();
 ++read_rows, row += row_increment) {
        file.read(reinterpret_cast<char *>(&pixels_[row * width()]),
                  width() * sizeof(pixel));
        file.seekg(padding, ifstream::cur);
    }
}

void
bmp::write_pixels_(ofstream &file) {
    uint32_t padding = dib_header_.compute_padding();
    char padding_bytes[] = {0, 0, 0};
    for (int32_t row = height() - 1; row >= 0; --row) {
        file.write(reinterpret_cast<char *>(&pixels_[row * width()]),
                   width() * sizeof(pixel));
        file.write(padding_bytes, padding);
    }
}
