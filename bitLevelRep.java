import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;

public class bitLevelRep { // Ahmet Önkol 150117018 
	public static void main(String args[]) {
		Scanner input = new Scanner(System.in);
		System.out.println("Enter the input file name:");
		String fileName = input.nextLine();
		try {
			File file = new File(fileName); // creates a new file instance
			FileReader fr = new FileReader(file); // reads the file
			FileWriter writer = new FileWriter("output.txt", false);
			BufferedReader br = new BufferedReader(fr); // creates a buffering character input stream
			StringBuffer sb = new StringBuffer(); // constructs a string buffer with no characters
			String line , bitsStr2 = "";
			System.out.print("Please enter the byte ordering: Ex: 'little endian'-'big endian'");
			String byteOrder = input.nextLine();
			System.out.print("Please enter the Floating point size: (A number between 1 and 4)");
			int floatSize = input.nextInt();
			while ((line = br.readLine()) != null) {
				sb.append(line); // appends line to string buffer
				sb.append("\n"); // line feed
				if (line.contains("u")) {  //if the number is unsigned
					String intValue = line.replaceAll("[^0-9]", ""); //remove u from the number to calculate it's corresponding values
					if (byteOrder.contains("big") || byteOrder.contains("Big")) {  // if byte order is big endian
						int x = Integer.parseInt(intValue); //parse the string to int value
						String s = fromDeci(16, x); // function will return hexadecimal version of the number
						int a = 4 - s.length();
						String hexStr = "";
						for (int m = 0; m < a; m++) {
							hexStr += 0; // if there are leading zeros that are missing add them to left of the hexStr
						}
						hexStr += s;
						for (int j = 0; j < hexStr.length() && j + 1 < hexStr.length(); j++) {
							writer.write(Character.toString(hexStr.charAt(j)) + Character.toString(hexStr.charAt(j + 1))  //writes hexadecimal values in big endian order
									+ " ");
							j++;
						}
						writer.write("\n");
					}

					if (byteOrder.contains("little") || byteOrder.contains("Little")) { // if byte order is little endian
						int x = Integer.parseInt(intValue); //parse the string to int value
						String s = fromDeci(16, x);   // function will return hexadecimal version of the number
						int a = 4 - s.length();
						String hexStr = "";
						for (int m = 0; m < a; m++) {
							hexStr += 0; // if there are leading zeros that are missing add them to left of the hexStr
						}
						hexStr += s;
						for (int j = hexStr.length() - 1; j >= 0 && j - 1 >= 0; j--) {
							writer.write(Character.toString(hexStr.charAt(j - 1)) + Character.toString(hexStr.charAt(j))  //writes hexadecimal values in little endian order
									+ " ");
							j--;
						}
						writer.write("\n");
					}
				} else if (line.contains(".")) { // if the number is floating point number
					int negative = 0;
					if (line.contains("-")) {
						line = line.substring(1); // if the number is negative, remove "-" part so that it can be fit into decimalToBinary() function which will convert floating point number to binary
						negative = 1;
					}
					String binary = floatToBinary(new BigDecimal(line));
					if (!binary.contains(".")) {
						binary = binary + ".0";
					}
					int mantissa = 0; // for finding E
					int dotPlace = 0; // for finding E
					for (int i = 0; i < binary.length(); i++) {
						if (Character.toString(binary.charAt(i)).equals("1")) {
							if (i == 0) { // this for loop's purpose is to find the first 1 that appears in binary format of the number.So that we can compute E (E = exp - bias)
								mantissa = 1;
								break;
							}
							mantissa = i;
							break;
						}
					}
					for (int i = 0; i < binary.length(); i++) {
						if (Character.toString(binary.charAt(i)).equals(".")) {
							dotPlace = i; // this for loop will locate the dot's place so we can compute E.
							break;
						}
					}
					binary = binary.replace(".", ""); // erase the dot character. So we are able to place a new one for finding mantissa

					int E = dotPlace - mantissa;
					double bias = 0;
					// if else cases in below section will compute corresponding bias values depending on floatSize that is entered by user
					if (floatSize == 1) {
						bias = Math.pow(2, 4 - 1) - 1;

					} else if (floatSize == 2) {
						bias = Math.pow(2, 6 - 1) - 1;
					} else if (floatSize == 3) {
						bias = Math.pow(2, 8 - 1) - 1;
					} else if (floatSize == 4) {
						bias = Math.pow(2, 10 - 1) - 1;
					}
					int exponent = (int) (E + bias); // simply calculates exponent part of the number
					String exp = floatToBinary(new BigDecimal(exponent)) ; // binary version of exponent will be stored in bin variable
					String subMantissa = null, fraction = null;
					if (exponent == 0) { // if the exponent is zero (Denormalized) set the mantissa part as 0.001.... 
						for (int i = 0; i < binary.length(); i++) {
							if (Character.toString(binary.charAt(i)).equals("1")) {
								subMantissa = "0." + binary.substring(i);
								for (int j = 1; j < 5; j++) {
									if (j == floatSize) {
										int d = 2 * floatSize + 2;
										for (int x = 1; x < d; x++) {
											exp += "0"; // add zeros if bits don't have correct digit number
										}
									}
								}
								break;
							}
						}
					}
					if (exponent != 0) { // if exponent is not zero then simply get the mantissa
						for (int i = 0; i < binary.length(); i++) {
							if (Character.toString(binary.charAt(i)).equals("1")) {
								subMantissa = "1." + binary.substring(i + 1);
								break;
							}
						}

					}
					for (int i = 0; i < binary.length(); i++) { // in this part using subMantissa data,finally we are able to take fraction part 
						if (Character.toString(subMantissa.charAt(i)).equals(".")) {
							fraction = subMantissa.substring(i + 1);
							break;
						}
					}

					if (floatSize == 1) {
						if (fraction.length() > 3) { // if it extends fraction bit number
							if (Character.toString(fraction.charAt(3)).equals("1")) { // if the bit at the end of extension is equal to 1 , then it will simply round the fraction to nearest even
								String one = "1";
								String str = fraction.substring(0, 3);
								fraction = addBinary(str, one);
							} else if (!Character.toString(fraction.charAt(3)).equals("1")) { // if it is not 1, then take the part that is required for expression only.
								fraction = fraction.substring(0, 3);
							}

						} else if (fraction.length() < 3) { // if digit number for bits that are needed for expression is not enough then simply add zeros to least significant bits.
							int x = 3 - fraction.length();
							for (int i = 0; i < x; i++) {
								fraction += "0";
							}

						}
					} else if (floatSize == 2) {  // same process will be applied for float size 2
						if (fraction.length() > 9) { 
							if (Character.toString(fraction.charAt(9)).equals("1")) {
								String one = "1";
								String str = fraction.substring(0, 9);
								fraction = addBinary(str, one);
							} else if (!Character.toString(fraction.charAt(9)).equals("1")) {
								fraction = fraction.substring(0, 9);
							}
						} else if (fraction.length() < 9) {
							int x = 9 - fraction.length();
							for (int i = 0; i < x; i++) {
								fraction += "0";
							}

						}

					}

					else if (floatSize == 3) {
						if (fraction.length() > 13) { // same process will be applied for float size 3
							if (Character.toString(fraction.charAt(13)).equals("1")) { // but in this part we are using 13 bits to express the fraction part.
								String one = "1";
								String str = fraction.substring(0, 13); 
								fraction = addBinary(str, one) + "00";
							} else if (!Character.toString(fraction.charAt(13)).equals("1")) {
								fraction = fraction.substring(0, 13) + "00";
							}
						} else if (fraction.length() < 13) {
							int x = 15 - fraction.length();
							for (int i = 0; i < x; i++) {
								fraction += "0";
							}

						}
					}

					else if (floatSize == 4) { // same process will be applied for float size 4
						if (fraction.length() > 13) { // in this part fraction part uses 13 bits too.
							if (Character.toString(fraction.charAt(13)).equals("1")) {
								String one = "1";
								String str = fraction.substring(0, 13);
								fraction = addBinary(str, one) + "00000000";
							} else if (!Character.toString(fraction.charAt(13)).equals("1")) {
								fraction = fraction.substring(0, 13) + "00000000";
							}
						} else if (fraction.length() < 13) {
							int x = 21 - fraction.length();
							for (int i = 0; i < x; i++) {
								fraction += "0";
							}

						}
					}
					String binToHex = "";
					if (negative == 0) {
						binToHex = "0" + exp + fraction; // if the number is positive add 0 to most significant bit
					} else if (negative == 1) {
						binToHex = "1" + exp + fraction; // if the number is negative add 1 to most significant bit
					}
					int valx = BTD(binToHex); // it will convert binary number to decimal number
					String hexStr1 = fromDeci(16, valx); // it will convert decimal number to hexadecimal one.
					if (byteOrder.contains("big") || byteOrder.contains("Big")) { // if byte order is big endian
						for (int j = 0; j < hexStr1.length() && j + 1 < hexStr1.length(); j++) {
							writer.write(Character.toString(hexStr1.charAt(j))      // writes corresponding data to output file
									+ Character.toString(hexStr1.charAt(j + 1)) + " ");
							j++;
						}
						writer.write("\n");
					}

					else if (byteOrder.contains("little") || byteOrder.contains("Little")) { // if byte order is little endian
						for (int j = hexStr1.length() - 1; j >= 0 && j - 1 >= 0; j--) {
			 				writer.write(Character.toString(hexStr1.charAt(j - 1)) // writes corresponding data to output file
									+ Character.toString(hexStr1.charAt(j)) + " ");
							j--;
						}
						writer.write("\n");
					}
				}

				else if (!line.contains(".") && !line.contains("u")) { // if it not neither unsigned integer nor floating point number; in that case it will be signed integer
					if (line.contains("-")) {  // if the number is negative signed integer
						String str = "";
						line = line.substring(1);  // simply erase "-" character so that code will fit to functions
						byte[] bits = toBinary(line); // this function will return binary versions of corresponding number
						for (int i = bits.length - 1; i >= 0; i--) {
							bitsStr2 += bits[i];
						}
						for (int j = 0; j < bitsStr2.length(); j++) { // firstly it will take the negative version of the number with changing bits
							if (Character.toString(bitsStr2.charAt(j)).equals("1")) {
								str += 0; // if the corresponding bit is 1 , make it 0
							} else if (Character.toString(bitsStr2.charAt(j)).equals("0")) {
								str += 1;  // if the corresponding bit is 0, make it 1
							}
						}
						String one = "1";
						String hexVal = addBinary(str, one); // add 1 to number that is found above.So that we can apply same process like the number is unsigned integer
						int valx = BTD(hexVal); // returns decimal version of corresponding binary number
						String hexStr1 = fromDeci(16, valx); // returns hexadecimal version of corresponding decimal number
						if (byteOrder.contains("big") || byteOrder.contains("Big")) { // if byte order is big endian
							for (int j = 0; j < hexStr1.length() && j + 1 < hexStr1.length(); j++) {
								writer.write(Character.toString(hexStr1.charAt(j)) //writes corresponding data to output file
										+ Character.toString(hexStr1.charAt(j + 1)) + " ");
								j++;
							}
							writer.write("\n");
						}

						else if (byteOrder.contains("little") || byteOrder.contains("Little")) { // if byte order is little endian
							for (int j = hexStr1.length() - 1; j >= 0 && j - 1 >= 0; j--) {
								writer.write(Character.toString(hexStr1.charAt(j - 1)) // writes corresponding data to output file
										+ Character.toString(hexStr1.charAt(j)) + " ");
								j--;
							}
							writer.write("\n");
						}

					} else if (!line.contains("-")) { // exactly the same process with unsigned integer part
						if (byteOrder.contains("big") || byteOrder.contains("Big")) { // if byte order is big endian
							int x = Integer.parseInt(line); // parse the string to int
							String s = fromDeci(16, x); // takes hexadecimal value
							int a = 4 - s.length();
							String hexStr = "";
							for (int m = 0; m < a; m++) {
								hexStr += 0; // put zeros to leading bits if necessary
							}
							hexStr += s;
							for (int j = 0; j < hexStr.length() && j + 1 < hexStr.length(); j++) {
								writer.write(Character.toString(hexStr.charAt(j))
										+ Character.toString(hexStr.charAt(j + 1)) + " "); // writes corresponding data to output file
								j++;
							}
							writer.write("\n");
						}
						if (byteOrder.contains("little") || byteOrder.contains("Little")) { // if byte order is little endian
							int x = Integer.parseInt(line); // parse the string to int
							String s = fromDeci(16, x); // takes hexadecimal value
							int a = 4 - s.length();
							String hexStr = "";
							for (int m = 0; m < a; m++) {
								hexStr += 0;  // put zeros to leading bits if necessary
							}
							hexStr += s;																							
							for (int j = hexStr.length() - 1; j >= 0 && j - 1 >= 0; j--) {
								writer.write(Character.toString(hexStr.charAt(j - 1)) // writes corresponding data to output file
										+ Character.toString(hexStr.charAt(j)) + " ");
								j--;
							}
							writer.write("\n");
						}

					}

				}

			}

			writer.close();
			fr.close(); // closes the stream and release the resources
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String floatToBinary(BigDecimal decimal) { // conversion for floating point numbers to binary 
		return floatToBinary(decimal, 50);
	}

	public static String floatToBinary(BigDecimal decimal, int digits) { // converts floating point number to binary number 
		if(decimal.toString().equals("0")) {
		String s = "0" ;
		return s ;
		}
		BigDecimal integer = decimal.setScale(0, RoundingMode.FLOOR);
		BigDecimal fractional = decimal.subtract(integer);

		StringBuilder sb = new StringBuilder();

		// Integer part
		BigDecimal two = BigDecimal.valueOf(2);
		BigDecimal zero = BigDecimal.ZERO;
		while (integer.compareTo(zero) > 0) {
			BigDecimal[] result = integer.divideAndRemainder(two);
			sb.append(result[1]);
			integer = result[0];
		}
		sb.reverse();

		// Fractional part
		int count = 0;
		if (fractional.compareTo(zero) != 0) {
			sb.append(".");
		}
		while (fractional.compareTo(zero) != 0) {
			count++;
			fractional = fractional.multiply(two);
			sb.append(fractional.setScale(0, RoundingMode.FLOOR));
			if (fractional.compareTo(BigDecimal.ONE) >= 0) {
				fractional = fractional.subtract(BigDecimal.ONE);
			}
			if (count >= digits) {
				break;
			}
		}

		return sb.toString();
	}

	public static byte[] toBinary(String number) { // binary conversion for integers
		byte[] binary = new byte[16];
		int index = 0;
		int copyOfInput = Integer.parseInt(number);
		;
		while (copyOfInput > 0) {
			binary[index++] = (byte) (copyOfInput % 2);
			copyOfInput = copyOfInput / 2;
		}

		return binary;
	}
	
	static String addBinary(String a, String b) { // adds 2 binary numbers and returns as string variable 
		// i used this function for adding 1 in case of rounding to nearest even and conversion for negative signed number to positive version of it

		// Initialize result
		String result = "";

		// Initialize digit sum
		int s = 0;

		// Traverse both strings starting
		// from last characters
		int i = a.length() - 1, j = b.length() - 1;
		while (i >= 0 || j >= 0 || s == 1) {

			// Comput sum of last
			// digits and carry
			s += ((i >= 0) ? a.charAt(i) - '0' : 0);
			s += ((j >= 0) ? b.charAt(j) - '0' : 0);

			// If current digit sum is
			// 1 or 3, add 1 to result
			result = (char) (s % 2 + '0') + result;

			// Compute carry
			s /= 2;

			// Move to next digits
			i--;
			j--;
		}

		return result;
	}

	
	public static int BTD(String bin) { // conversion for binary number to decimal version of it
		int result = 0;

		for (int i = 0; i < bin.length(); i++) {
			result = (result * 2) + (bin.charAt(i) - '0');
		}
		return result;
	}

	public static char reVal(int num) {
		if (num >= 0 && num <= 9)
			return (char) (num + 48);
		else
			return (char) (num - 10 + 65);
	}
	
	// Function to convert a given decimal number
	// to a base 'base' and
	public static String fromDeci(int base1, int inputNum) { // converts decimal number to hexadecimal one.
		String s = "";

		// Convert input number is given
		// base by repeatedly dividing it
		// by base and taking remainder
		while (inputNum > 0) {
			s += reVal(inputNum % base1);
			inputNum /= base1;
		}
		StringBuilder ix = new StringBuilder();

		// append a string into StringBuilder input1
		ix.append(s);

		// Reverse the result
		return new String(ix.reverse());
	}

}