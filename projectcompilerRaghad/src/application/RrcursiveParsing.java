package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RrcursiveParsing {
	private Token currentToken;
	private StringBuilder stoken;
	private int lineNumber;

	private static List<String> reservedWordsList = Arrays.asList("project", "const", "var", "subroutine", "start",
			"end", "input", "output", "if", "then", "else", "endif", "loop", "do");
	private static ArrayList<String> reservedWords = new ArrayList<>(reservedWordsList);

	public RrcursiveParsing(StringBuilder tokenList) {
		stoken = tokenList;
		currentToken = getToken();
		this.lineNumber = lineNumber;
	}

	// main for my production
	public void mycode() {
		project_declaration();

		if (currentToken.getToken().equalsIgnoreCase("$")) {
			System.out.println();
			System.out.println("Successful parsing .");
		} else {
			System.out.println();
			System.out.println("Error.....!!!!");
		}
	}

	private Token getToken() {
		int x = stoken.indexOf(" ");
		String sub = stoken.substring(0, x);
		stoken.delete(0, x + 1);
		Token token = new Token(sub, lineNumber); // Create a new Token object with the line number
		lineNumber++;
		return token;
	}

//Report Error massge
	public void reportError(String message, Token token) {
		System.out.println("Line " + token.getLineNumber() + ": Oops! " + message);

		System.out.println("Token: " + token.getToken());

		System.exit(1);
	}
//My production rules  in the  EBNF 
	// project_declaration -> project_def "."

	public void project_declaration() {
		project_def();
		if (currentToken.getToken().equals(".")) {
			currentToken = getToken();
		} else {
			reportError("Required '.' at the end of project declaration.", currentToken);
		}
	}

	// project_def -> project_heading declarations compound_stmt
	public void project_def() {
		project_heading();
		declarations();
		compound_stmt();
	}
//    project_heading -> "project" "name" ";"

	public void project_heading() {
		if (currentToken.getToken().equalsIgnoreCase("project")) {
			currentToken = getToken();
			String name = currentToken.getToken();
			if (isValidName(name)) {
				currentToken = getToken();
				if (currentToken.getToken().equals(";")) {
					currentToken = getToken();
				} else {
					reportError("Required ';' after project name.", currentToken);
				}
			} else {
				reportError("Required valid name in project heading.", currentToken);
			}
		} else {
			reportError("Required 'project' in project heading.", currentToken);
		}
	}

//    declarations -> const_decl var_decl subroutine_decl
	public void declarations() {
		const_decl();
		var_decl();
		subroutine_decl();
	}

	// const_decl -> "const" ( const_item ";" )+ | ε
	public void const_decl() {
		if (currentToken.getToken().equalsIgnoreCase("const")) {
			currentToken = getToken();
			while (!currentToken.getToken().equals("var") && !currentToken.getToken().equals("subroutine")
					&& !currentToken.getToken().equals("start")) {
				String name = currentToken.getToken();
				if (isValidName(name)) {
					currentToken = getToken();
					if (currentToken.getToken().equals("=")) {
						currentToken = getToken();
						String value = currentToken.getToken();
						if (isValidIntegerValue(value)) {
							currentToken = getToken();
						} else {
							reportError("Required just 'integer-value' in constant declaration.", currentToken);
						}
					} else {
						reportError("Required '=' in constant declaration.", currentToken);
					}
				} else {
					reportError("Required valid name in constant declaration.", currentToken);
				}
			}
		}
	}
//    var_decl -> "var" ( var_item ";" )+ | ε

	public void var_decl() {
		if (currentToken.getToken().equalsIgnoreCase("var")) {
			currentToken = getToken();
			while (!currentToken.getToken().equals("subroutine") && !currentToken.getToken().equals("start")) {
				var_item();
			}
		} else {
			// Empty production
		}
	}

//    var_item -> name_list ":" "int"
	public void var_item() {
		name_list();
		if (currentToken.getToken().equals(":")) {
			currentToken = getToken();
			if (currentToken.getToken().equalsIgnoreCase("int")) {
				currentToken = getToken();
			} else {
				reportError("Required 'int' in variable declaration.", currentToken);
			}
		} else {
			reportError("Required ':' in variable declaration.", currentToken);
		}
	}

	// name_list -> "name" ( "," "name" )*

	public void name_list() {
		String name = currentToken.getToken();
		if (!isValidName(name)) {
			reportError("Required 'name' in name list.", currentToken);
		}

		while (currentToken.getToken().equals(",")) {
			currentToken = getToken();
			name = currentToken.getToken();
			if (!isValidName(name)) {
				reportError("Required 'name' in name list.", currentToken);
			}
		}

		if (!currentToken.getToken().equals(";")) {
			reportError("Required ';' after name list.", currentToken);
		} else {
			currentToken = getToken();
		}
	}
//    subroutine_decl -> subroutine_heading declarations compound_stmt ";" | ε

	public void subroutine_decl() {
		if (currentToken.getToken().equalsIgnoreCase("subroutine")) {
			currentToken = getToken();
			subroutine_heading();
			declarations();
			compound_stmt();
			if (currentToken.getToken().equals(";")) {
				currentToken = getToken();
			} else {
				reportError("Required ';' after subroutine declaration.", currentToken);
			}
		}
	}

//    subroutine_heading -> "routine" "name" ";"
	public void subroutine_heading() {
		if (currentToken.getToken().equalsIgnoreCase("routine")) {
			currentToken = getToken();
			String name = currentToken.getToken();
			if (!isValidName(name)) {
				reportError("Required 'name' in subroutine heading.", currentToken);
			}

			if (currentToken.getToken().equals(";")) {
				currentToken = getToken();
			} else {
				reportError("Required ';' after subroutine name.", currentToken);
			}
		} else {
			reportError("Required 'routine' in subroutine heading.", currentToken);
		}
	}

//    compound_stmt -> "start" stmt_list "end"
	public void compound_stmt() {
		if (currentToken.getToken().equalsIgnoreCase("start")) {
			currentToken = getToken();
			stmt_list();
			if (currentToken.getToken().equalsIgnoreCase("end")) {
				currentToken = getToken();
			} else {
				reportError("Required 'end' at the end of compound statement.", currentToken);
			}
		} else {
			reportError("Required 'start' at the beginning of compound statement.", currentToken);
		}

		if (!currentToken.getToken().equals(";")) {
			reportError("Required ';' after compound statement.", currentToken);
		} else {
			currentToken = getToken();
		}
	}

	// stmt_list -> ( statement ";" )
	public void stmt_list() {
		while (!currentToken.getToken().equalsIgnoreCase("end") && !currentToken.getToken().equalsIgnoreCase("else")
				&& !currentToken.getToken().equalsIgnoreCase("endif")
				&& !currentToken.getToken().equalsIgnoreCase("loop")) {
			statement();
			if (currentToken.getToken().equals(";")) {
				currentToken = getToken();
			} else {
				reportError("Required ';' after statement.", currentToken);
			}
		}
	}

	/// *statement -> ass_stmt | inout_stmt | if_stmt | loop_stmt | compound_stmt |
	/// ε

	public void statement() {
		if (currentToken.getToken().equalsIgnoreCase("if")) {
			if_stmt();
		} else if (currentToken.getToken().equalsIgnoreCase("loop")) {
			loop_stmt();
		} else if (isValidName(currentToken.getToken())) {
			ass_stmt();
		} else if (currentToken.getToken().equalsIgnoreCase("input")) {
			inout_stmt();
		} else if (currentToken.getToken().equalsIgnoreCase("output")) {
			inout_stmt();
		} else if (currentToken.getToken().equals("(")) {
			compound_stmt();
		} else {
			currentToken = getToken();
		}
	}

//loop_stmt -> "loop" "(" bool_exp ")" "do" statement
	public void loop_stmt() {
		if (currentToken.getToken().equalsIgnoreCase("loop")) {
			currentToken = getToken();
			if (currentToken.getToken().equals("(")) {
				currentToken = getToken();
				bool_exp();
				if (currentToken.getToken().equals(")")) {
					currentToken = getToken();
					if (currentToken.getToken().equalsIgnoreCase("do")) {
						currentToken = getToken();
						statement();
					} else {
						reportError("Required 'do' after loop statement.", currentToken);
					}
				} else {
					reportError("Required ')' in loop statement.", currentToken);
				}
			} else {
				reportError("Required '(' in loop statement.", currentToken);
			}
		}
	}
//if_stmt -> "if" "(" bool_exp ")" "then" statement else_part "endif"

	public void if_stmt() {
		if (currentToken.getToken().equalsIgnoreCase("if")) {
			currentToken = getToken();
			if (currentToken.getToken().equals("(")) {
				currentToken = getToken();
				bool_exp();
				if (currentToken.getToken().equals(")")) {
					currentToken = getToken();
					if (currentToken.getToken().equalsIgnoreCase("then")) {
						currentToken = getToken();
						statement();
						else_part();
						if (currentToken.getToken().equalsIgnoreCase("endif")) {
							currentToken = getToken();
						} else {
							reportError("Required 'endif' after if statement.", currentToken);
						}
					} else {
						reportError("Required 'then' after if statement.", currentToken);
					}
				} else {
					reportError("Required ')' in if statement.", currentToken);
				}
			} else {
				reportError("Required '(' in if statement.", currentToken);
			}
		}
	}
//ass_stmt -> "name" ":=" arith_exp

	public void ass_stmt() {
		if (isValidName(currentToken.getToken())) {
			currentToken = getToken();
			if (currentToken.getToken().equals(":=")) {
				currentToken = getToken();
				arith_exp();
			} else {
				reportError("Required ':=' in assignment statement.", currentToken);
			}
		} else {
			reportError("Required valid name in assignment statement.", currentToken);
		}
	}
	// inout_stmt -> "input" "(" "name" ")" | "output" "(" name_value ")"

	public void inout_stmt() {
		if (currentToken.getToken().equalsIgnoreCase("input")) {
			currentToken = getToken();
			if (currentToken.getToken().equals("(")) {
				currentToken = getToken();
				if (isValidName(currentToken.getToken())) {
					currentToken = getToken();
					if (currentToken.getToken().equals(")")) {
						currentToken = getToken();
					} else {
						reportError("Required ')' after input statement.", currentToken);
					}
				} else {
					reportError("Required valid name in input statement.", currentToken);
				}
			} else {
				reportError("Required '(' in input statement.", currentToken);
			}
		} else if (currentToken.getToken().equalsIgnoreCase("output")) {
			currentToken = getToken();
			if (currentToken.getToken().equals("(")) {
				currentToken = getToken();
				name_value();
				if (currentToken.getToken().equals(")")) {
					currentToken = getToken();
				} else {
					reportError("Required ')' after output statement.", currentToken);
				}
			} else {
				reportError("Required '(' in output statement.", currentToken);
			}
		}
	}
	// arith_exp -> term ( add_sign term )*

	public void arith_exp() {
		term();
		while (currentToken.getToken().equals("+") || currentToken.getToken().equals("-")) {
			add_sign();
			term();
		}
	}
	// add_sign -> "+" | "-"

	public void add_sign() {
		if (currentToken.getToken().equals("+")) {
			currentToken = getToken();
		} else if (currentToken.getToken().equals("-")) {
			currentToken = getToken();
		} else {
			reportError("Required '+' or '-' after term.", currentToken);
		}
	}
//term -> factor ( mul_sign factor )*

	public void term() {
		factor();
		// mul_sign -> "*" | "/" | "%"

		while (currentToken.getToken().equals("*") || currentToken.getToken().equals("/")
				|| currentToken.getToken().equals("%")) {
			currentToken = getToken();
			factor();
		}
	}

	// factor -> "(" arith_exp ")" | name_value

	public void factor() {
		if (currentToken.getToken().equals("(")) {
			currentToken = getToken();
			arith_exp();
			if (currentToken.getToken().equals(")")) {
				currentToken = getToken();
			} else {
				reportError("Required ')' in factor.", currentToken);
			}
		} else if (isValidName(currentToken.getToken()) || isValidIntegerValue(currentToken.getToken())) {
			currentToken = getToken();
		} else {
			reportError("Required '(' or 'name' or 'integer-value' in factor.", currentToken);
		}
	}

//    name_value -> "name" | "integer-value"
	public void name_value() {
		if (isValidName(currentToken.getToken())) {
			currentToken = getToken();
		} else if (isValidIntegerValue(currentToken.getToken())) {
			currentToken = getToken();
		} else {
			reportError("Invalid value: " + currentToken.getToken(), currentToken);
		}
	}
	// bool_exp -> name_value relational_oper name_value

	public void bool_exp() {
		arith_exp();
		// relational_oper -> "=" | "<>" | "<" | "<=" | ">" | ">="
		if (currentToken.getToken().equalsIgnoreCase("<") || currentToken.getToken().equalsIgnoreCase(">")
				|| currentToken.getToken().equalsIgnoreCase("==") || currentToken.getToken().equalsIgnoreCase("<>")
				|| currentToken.getToken().equalsIgnoreCase("<=") || currentToken.getToken().equalsIgnoreCase(">=")) {
			currentToken = getToken();
			arith_exp();
		} else {
			reportError("Required relational operator in boolean expression.", currentToken);
		}
	}
//    else_part -> "else" statement | ε

	public void else_part() {
		if (currentToken.getToken().equalsIgnoreCase("else")) {
			currentToken = getToken();
			statement();
		} else {
			// Empty production "lambda"
		}
	}

	private boolean isReservedWord(String token) {
		return reservedWords.contains(token.toLowerCase());
	}

	public boolean isValidName(String token) {
		// Check if the token is a valid name and not a reserved word
		return token.matches("[a-zA-Z][a-zA-Z0-9]*") && !isReservedWord(token);
	}

//check if token any integer value 
	public boolean isValidIntegerValue(String token) {
		try {
			Integer.parseInt(token);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
