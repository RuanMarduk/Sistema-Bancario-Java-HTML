public class ValidaCPF {

    public static boolean validaCPF(String cpf) {
        cpf = cpf.replace(".", "").replace("-", "");
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;

        try {
            int soma1 = 0, soma2 = 0;
            for (int i = 0; i < 9; i++) {
                int num = Character.getNumericValue(cpf.charAt(i));
                soma1 += num * (10 - i);
                soma2 += num * (11 - i);
            }

            int dig1 = soma1 % 11;
            dig1 = (dig1 < 2) ? 0 : 11 - dig1;
            soma2 += dig1 * 2;

            int dig2 = soma2 % 11;
            dig2 = (dig2 < 2) ? 0 : 11 - dig2;

            return dig1 == Character.getNumericValue(cpf.charAt(9)) &&
                    dig2 == Character.getNumericValue(cpf.charAt(10));
        } catch (Exception e) {
            return false;
        }
    }



}
