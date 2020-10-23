### Bit Level Representations
bit level conversions and representations of numbers.

### Guideline for Understanding The Scope of The Project

The size of the data type can be 1, 2, 3, 4, or 6 bytes.
- The signed integers are written as normal integers in the file. (e.g., 3, -5, etc.)
- The unsigned integers are written as integers followed by the letter u. (e.g., 3u, 5u,
etc.)
- The floating point numbers are written with a decimal point. (e.g., 3.0, 5.987, etc.)
- If the read data type is signed integer, your program will convert the numbers in the
input file using 2’s complement representation.
- If the read data type is unsigned integer, numbers will be converted using unsigned
integer representation.
- If the read type is floating point number, you will use IEEE-like format. The number of
exponent bits according to given data size will be like the following:
    - if 1 byte (i.e., 8 bits), 4 bits will be used for exponent part
    - if 2 bytes (i.e., 16 bits), 6 bits will be used for exponent part
    - if 3 bytes (i.e., 24 bits), 8 bits will be used for exponent part
    - if 4 bytes (i.e., 32 bits), 10 bits will be used for exponent part
    - While calculating the mantissa to get the floating point value, you will only use
the first 13 bits of the fraction part (for 3-byte and 4-byte data sizes). You
will use “round to nearest even” method for rounding fraction bits to 13 bits.
Details about the program are listed below:
- At the beginning of the execution, your program will prompt for the input file
### Sample Execution Scenario



![SYSTEM-8N%C4%B0SAN pdf](https://user-images.githubusercontent.com/62245004/97060995-f8041d00-159d-11eb-9b74-c055ae542f40.png)
