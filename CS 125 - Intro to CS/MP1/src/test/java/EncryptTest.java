import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test suite for the Encrypt class.
 * <p>
 * The provided test suite is correct and complete. You should not need to modify it. However, you
 * should understand it. You will need to augment or write test suites for later MPs.
 *
 * @see <a href="https://cs125.cs.illinois.edu/MP/3/">MP3 Documentation</a>
 */
public class EncryptTest {

    /** Timeout for our tests. Solution takes 29 ms. */
    private static final int TEST_TIMEOUT = 300;

    private static final Map<EncryptInput, String> PRECOMPUTED_RESULTS = //
            new LinkedHashMap<>();

    /**
     * Test encryption and decryption.
     */
    @Test(timeOut = TEST_TIMEOUT)
    public void testEncryptAndDecrypt() throws UnsupportedEncodingException {


        for (Map.Entry<EncryptInput, String> precomputedResult : PRECOMPUTED_RESULTS.entrySet()) {

            EncryptInput input = precomputedResult.getKey();
            String precomputedOutput = precomputedResult.getValue();
            if (precomputedOutput != null) {
                precomputedOutput = URLDecoder.decode(precomputedOutput, "utf-8");
            }

            /*
             * Pull values from the JSON object.
             */
            char[] line = URLDecoder.decode(input.line, "utf-8").toCharArray();

            char[] expectedOutput = null;
            if (precomputedOutput != null) {
                expectedOutput = precomputedOutput.toCharArray();
                Assert.assertEquals(line.length, precomputedOutput.length());
            }
            char[] copyOfLine = line.clone();
            char[] transformedLine;
            if (input.encrypt) {
                transformedLine = Encrypt.encrypt(line, input.shift);
            } else {
                transformedLine = Encrypt.decrypt(line, input.shift);
            }
            if (transformedLine != null) {
                Assert.assertEquals(copyOfLine.length, transformedLine.length);
            }
            /*
             * Ensure that encrypt and decrypt do not modify the original array
             */
            Assert.assertFalse(line == transformedLine);
            Assert.assertTrue(Arrays.equals(line, copyOfLine));
            Assert.assertTrue(String.valueOf(copyOfLine).equals(String.valueOf(line)));

            /*
             * Check expected output
             */
            if (expectedOutput != null) {
                Assert.assertNotNull(transformedLine);
                Assert.assertTrue(Arrays.equals(transformedLine, expectedOutput),
                        String.valueOf(transformedLine) + " != " + String.valueOf(expectedOutput));
            } else {
                Assert.assertNull(transformedLine);
            }
            /*
             * Stopping point for invalid values
             */
            if (expectedOutput == null) {
                continue;
            }
            Assert.assertTrue(String.valueOf(expectedOutput)
                    .equals(String.valueOf(transformedLine)));

            /*
             * Now undo the transformation
             */
            char[] copyOfTransformedLine = transformedLine.clone();
            char[] reformedLine;
            if (input.encrypt) {
                reformedLine = Encrypt.decrypt(transformedLine, input.shift);
            } else {
                reformedLine = Encrypt.encrypt(transformedLine, input.shift);
            }

            /*
             * Ensure that encrypt and decrypt do not modify the original array
             */
            Assert.assertFalse(line == reformedLine);
            Assert.assertTrue(Arrays.equals(line, copyOfLine));
            Assert.assertTrue(String.valueOf(copyOfLine).equals(String.valueOf(line)));
            Assert.assertFalse(transformedLine == reformedLine);
            Assert.assertTrue(Arrays.equals(transformedLine, copyOfTransformedLine));
            Assert.assertTrue(String.valueOf(copyOfTransformedLine)
                    .equals(String.valueOf(transformedLine)));

            /*
             * Check expected output. We should be back to the original string.
             */
            Assert.assertNotNull(reformedLine);
            Assert.assertTrue(Arrays.equals(line, reformedLine));
            Assert.assertTrue(String.valueOf(reformedLine).equals(String.valueOf(line)));
        }
    }

    public static class EncryptInput {

        /** Line to encrypt or decrypt. */
        final String line;

        /** Whether to encrypt or decrypt. */
        final boolean encrypt;

        /** Encryption or decryption shift. */
        final int shift;

        EncryptInput(String line, boolean encrypt, int shift) {
            this.line = line;
            this.encrypt = encrypt;
            this.shift = shift;
        }
    }

    /** Initialization routine. */
    public EncryptTest() {
        EncryptTest.PRECOMPUTED_RESULTS
                .put(new EncryptInput("", true, 8), "");
        EncryptTest.PRECOMPUTED_RESULTS
                .put(new EncryptInput("a", true, 1), "b");
        EncryptTest.PRECOMPUTED_RESULTS
                .put(new EncryptInput("b", true, -1), "a");
        EncryptTest.PRECOMPUTED_RESULTS
                .put(new EncryptInput("b", false, 1), "a");
        EncryptTest.PRECOMPUTED_RESULTS
                .put(new EncryptInput("a", false, -1), "b");
        /* BEGIN AUTOGENERATED CODE */
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("abc", true, 1), "bcd");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("bcd", false, 1), "abc");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%24Mu%3C%23%7Dt'Pxq.~ZrTL%22JbRnj%3Bb(u%5B*m_0wHM%40%3Al%60jS9UigjU%3F.Lg.sule%24X~%3A%40Zh%40txctqd(6teZv6Z%7CH)%604Uf%5B-Ot~", true, -688), "l6%5E%25kf%5Do9aZvgC%5B%3D5j3K%3BWS%24Kp%5EDrVHx%6016)%23UIS%3C%22%3ERPS%3E(v5Pv%5C%5EUNlAg%23)CQ)%5DaL%5DZMp~%5DNC_~Ce1qI%7C%3EODu8%5Dg");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("s%C2%A9W%7C%22%0DS%0AN%3DFmxP%C2%A95%3Fn2V%5EVI%7D4%09T2W%5DkxNG%C2%A94F%26r%26q-%3D9%5Eq%24%09OS", true, -421), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("i%26ofR%7B.%3A%3C3ms~qxHh.x4(J%24mr%24EV'M%2FHaH%20U%3D%26P(eV%60Z(gz0%20L%5D3%3CQ)%5DsD'PZD_%22j%5Dvn%5E0D4(1%3FWDlVj7%7D%5Cx.%2BM(_%7D(.%7C-%3F(BADa%7D4%26*8OTC%7CvLc9", true, -981), "JfPG3%5Cnz%7CsNT_RY)InYth%2BdNSd%267g.o)B)%606%7Df1hF7A%3BhH%5Bp%60-%3Es%7C2i%3ET%25g1%3B%25%40bK%3EWO%3Fp%25thq%208%25M7Kw%5E%3DYnk.h%40%5Ehn%5Dm%20h%23%22%25B%5Etfjx05%24%5DW-Dy");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%7B%5DwB%60z%5D%3EII!!%2C%7C%3F%40%23-JN%7B8%3F%7Bmfmq8Om'P%2FlssdUA8%2C%26S%3BE%7BaehNi%26%2BnF(G%20d%40%25'eGSY%5Ej~Bl%7B%3AH4vBy%26%3C%5D%40II%7DsF%3E2sUVNk%3Cjjq%2F%3D'%7Byk%26~Qou)%3ApsbDfNpVwJ%23j", true, -652), ")j%25Om(jKVV..9*LM0%3AW%5B)EL)zsz~E%5Cz4%5D%3Cy!!qbNE93%60HR)nru%5Bv38%7BS5T-qM24rT%60fkw%2COy)GUA%24O'3IjMVV%2B!SK%3F!bc%5BxIww~%3CJ4)'x3%2C%5E%7C%236G%7D!oQs%5B%7Dc%25W0w");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("F%3A!%7B%3AJ4jEnO%23%2C0%60y)'%3Bc%7DT%608KI%5Dkppi.1t(w%2CQfC%3D%22K6%20B2", true, 265), "2%26lg%266%20V1Z%3Bnw%7BLetr'Oi%40L%2475IW%5C%5CUy%7C%60scw%3DR%2F)m7%22k.%7D");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("ef%C3%87%0DbBqEf(%09)LbpO_%3Er%7Be%C3%91W%7DfhgXm%7Cq'_%23u%2B9ov4%22w%0D%20wTc%C3%87nJnep%3CV%090hPGJ%5Ds%C3%91z%3F%098%C2%A9(W'd'%25%3F%0A%20%40%25d'n%C2%A9%5Cl", true, -199), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("hx-%2BhqZMz%5BOwVi%5DJh2TD%7B%2Fq2!%2503xr1iP.", true, -1735), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%C3%91bATn1M%C3%87%2B%0D%60%0AN%C3%87%7C(r%24%22.%C2%A9%09%C3%91%0Au%24B_nKJ!tsR%2F%C2%A9", true, -124), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("w%7B%3D%2Bk%26%23SU%5C_(ul%7C%3EY%404xjx%22M%3BFIRNbN%2Bwz4eDVI83%5C", false, -71), "_c%25rSmj%3B%3DDGo%5DTd%26A(%7B%60R%60i5%23.1%3A6J6r_b%7BM%2C%3E1%20zD");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%23S5C(%5D2Rt%60%22*Zj%3CkjZck%3E8%4061Ua%23WbGpr%20oHJTYB%60%25El%266t%7Cj2GhW5MH%25%606%40%2FD%3An%2Cgse_xx%24H!%23z", false, -68), "g8y(lBv7YEfn%3FO!PO%3FHP%23%7C%25zu%3AFg%3CG%2CUWdT-%2F9%3E'Ei*QjzYaOv%2CM%3Cy2-iEz%25s)~SpLXJD%5D%5Dh-eg_");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("F.%5DIiXS%7D%5BX%5Cp%24%24%3AFx_wyz7%3FL_8%3Ag%2Fd(60%7Bv%2BCP0JB4%5E", false, 147), "qY)t5%24~I'%24(%3COOeqD%2BCEFbjw%2Bce3Z0Sa%5BGBVn%7B%5Bum_*");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("S%09%7CJ%2C%20fr%22)%20s4%0D!HC%0D%5E%23MWE%C2%A9q580m%24%09Dq", false, 605), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("p%3D%23yL%3F%2Bzg4%5B%40nBF'%7D!aPDBMQY%2C%3CGxI%2F%3A%3Ebt%5B%25x%7CKaD9n%60D%5CD%2CXcLh~V%606rFf", true, 770), "zG-%24VI5%25q%3EeJxLP1(%2BkZNLW%5Bc6FQ%23S9DHl~e%2F%23'UkNCxjNfN6bmVr)%60j%40%7CPp");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("QS)cEobIo8Ge7%25)FHD~bqZ9(pr%60lICZf0!rS)", false, -342), "%2B-b%3D~I%3C%23Iq!%3Fp%5Eb%20%22%7DX%3CK4raJL%3AF%23%7C4%40iZL-b");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("dQU!x%2F)fIOizD%40V1M%7Di%7B%60vc6t.c!%7Bi2W%3D%3BiS7-UaL%40j_%7BK%7Bt%2Cto%7B%7Bz%2Cm%60'ig%23Oxc_vvO%3Cij~oE-o%5DWQ%207*Oaa1j4%20VDl.gGI1t%3B*vSm%2F", true, 189), "cPT%20w.(eHNhyC%3FU0L%7Chz_ub5s-b%20zh1V%3C%3AhR6%2CT%60K%3Fi%5EzJzs%2Bsnzzy%2Bl_%26hf%22Nwb%5EuuN%3Bhi%7DnD%2Cn%5CVP~6)N%60%600i3~UCk-fFH0s%3A)uRl.");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput(")Wnzq%0A%0AD%40%24tDc%09e*u%0D%26%5B%20OI%09zt%7DJYUNK~e%2Co%C2%A9Nv%5B%3DzK~u)tsPG(QAFMq7!QIV-0.%0A%3F%2CIS36%20ID8XN3%5DM%7C%3Eo8%3BGD'c", true, 791), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("*~e%3EG204pO_%5Dy%2BEEL%7B9p%5B_%3CGpyjAaBa%24%3C%40ZC%24c", false, -4562), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("w1%25~%7D%20MJv9%22wl%23%60MUq_spv%228%5E%20%5E%7DU5W%7C%3Et*%25adCD%7DD%5E%7D1MXE.N%3B99CM", true, 7853), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%3Bex6KB%7Bl-%2FR%7CKw%2B%7D5%7C%247MwKxg1vf8*Q~VMC!d", true, 182), "3%5Dp.C%3Asd%25'JtCo%23u-t%7B%2FEoCp_)n%5E0%22IvNE%3Bx%5C");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("1z%3D27%3Fg%C3%87%3C'RvQ%5BAb!%60K%3Dm2!WQT-5U%7BVK%C2%A9L%60%5CEL%C2%A9!%7Dmhw1x%3Bljk%7CmgT%C3%91%26%3DOQlAAUWJrtKGb%60%5C%0D%5EfLTdlo%2B_%25%3B_%5Bmkb%60IR%3E%23%5Emoiz%60KRTl6%3ChmIfAzg%20'P8~lQ%22", true, 7), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("U4_!rL%3Ck.%7BLND5Pi%3B%5E.l%24%600Sd%22%24DctZ)-e%3FQ%2F7GhCzLW%3DecS)hYW1kvUaN%2B%24l~8.4hs'rSyN%25%2FC1Ku%2B'%2Bo%5Bz0NE_%3C2yWvYB", false, 434), "~%5D)J%3Cue5WEuwm%5Ey3d(W6M*Y%7C.KMm-%3E%24RV%2FhzX%60p2lDu!f%2F-%7CR2%23!Z5%40~%2BwTM6HaW%5D2%3DP%3C%7CCwNXlZt%3FTPT9%25DYwn)e%5BC!%40%23k");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("av%7Cz82C~X9%0D%25E%5D%22%09%60%3ARi%3D%3Fo%7D%5EOuv%3B(SX-%403%3E%23k%242", true, 519), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("Xa%5E10-%C3%91Vnb%26'O%C3%91%0D%5Dq%25%C2%A9%7BH%0A_i%0D%0AY(%09%3A%C3%91f%0D1%09-", true, -772), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%26SDc50J%5C%3Awzz%40advb%7BrA7_Qj1o%5D%22E%7C140xQ%5E%3F%5C%26I%3Cej%23%20g%2BrBs5z", true, 810), "X%26v6gb%7C%2FlJMMr47I5NEsi2%24%3DcB0TwOcfbK%241q%2FX%7Bn8%3DUR%3A%5DEtFgM");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput(")'W%2B%5ED%C3%87Aef%236%40I%0Dk%7BJ%0A%09VW%C3%91etXtz%3ADgT%3F%7BE%5DD%C3%87%236!%24u%3E%2C%3D'%5D%2C", true, -938), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("C%23Iu%5EXGZ9%2FK%2BjfDSltM%20*6b%7BqzI%22z%3Da%2C~!nqi%3BBDiO%24%3A3%25_jGB6%5E%7DcCn%23%40%2Cf%3CBw4%5C%7DK8MT%24cDi%229%3F2%40f%7BFN%3A", true, -648), "T4Z'oiXkJ%40%5C%3C%7BwUd%7D%26%5E1%3BGs-%23%2CZ3%2CNr%3D02%20%23zLSUz%605KD6p%7BXSGo%2FtT%204Q%3DwMS)Em%2F%5CI%5Ee5tUz3JPCQw-W_K");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("a%3A%20%3EHh1%25W8%26Y3rn'mhR3qm8E%2C%3A%3F6AHDCynkIl%254Za)ZOJprcGzewWh%3Ag1%5C%2BBS9OvR%7DNWpj(%3CgeXb%5C%2C%3A%248HMZ!%3DhoCs)%22'%5C%3E%5D%5EuY%5EE%2CJ)(tIW%246j%3D%2FVv%40u-_qW", true, 6076), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("Td%5BC%200%7C%23yU4AEtXlkCCgC%60)%3BESYqT%3FUN9h%5Es)Jge17X9RT.%3FG8b%60", true, 75), "%40PG%2Fk%7BhneA%20-1%60DXW%2F%2FS%2FLt'1%3FE%5D%40%2BA%3A%25TJ_t6SQ%7C%23D%25%3E%40y%2B3%24NL");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("b28Gti5X%3F%22a%5C%40aUy%3FG%7CqZ%23%5C%3F6%3C%40yv%2CX%5CxcM%2F(q%5Dmzdv~%7B%20%2CT%7BL%60%5C%3D7Tr~s-*N%5B0%2CEnnT%5BMYvqn)d-e%3BAi6OoL*Prn5NH6)JebX%22!Em%5CqMm%5Ca'FY%2F6%5B%22q%5EL%5E%23%3DzLo%7CMd", true, -9083), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%40qo(Z%3B%24yb7HhR%5ENm4iYq%26F%40OcTsXn*k%24w'mP-S!8AI%20%3Cy%2FPVc7fhBESC%5Dvh%3F%3Cf%24%3FGLe%20k66%5E%40%3FVG(M.Hjfa.x%3D%7B", false, 4367), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("P%3F%23%7BB%246DmUESIZ%3A0I%2B%5C(O%5CaO3SWK31C)BpYXj_j)'r.ox%2F%7C%60f_63MQmc'%60UneZdrWob07uDQCBrQ7ao4wNozQT%20UKF_*%20%20%5B%3C", false, 1413), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("a1c%3ASm%5C!%405kUCn015ZmPvc%24_VjmMeS5X%40%24C4um!%7DSHDB%7CI2%2CQnPVjv7K%20%7BrCI88e~p0J~gKq%5E5X%7C%2Ch", true, 3586), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%3C%C3%87R%0DP0K~%C2%A9pmv%C3%87D'g%09%26cv)k%3E%26axH9k'%0DMv%3E%40%60g%C3%91%23%3CxM2'%09rMN%C3%91%C3%876gx%0D3%26%C3%91%0D%2B%C3%91s)%3Ecw%3D-%22U-%0A%C2%A9~%22", true, -972), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("nNs%23pB%60%5C%3Eh%5C%2BW%40IYW'H4%2B%3D~ejGHx%3C%23%26%7C%3C(x%60kF.OCBNm%7Cp%3FaUGS2", false, -138), "%3Ay%3FN%3Cm%2C(i4(V%23kt%25%23Rs_VhJ16rsDgNQHgSD%2C7qYznmy9H%3Cj-!r~%5D");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("8M)cN%3B3%60%7D%097'bA%22q%26G%5C%40g0S%7B%C3%91a2_P%3CQ%3FG%22E%3DzqHU-%5Ei%40dm%C2%A9EsY2MI%09%2CJ%3E~l%40T%3BT%3A%5BS%3E%2BEIs1%7C%3F~!1%40%5BP0gL2G%7BSH)%5EJsL%3B%268cBI7GxLt%25K%2C", true, -326), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("rYkj3wpBy%2CZpCw%5BAd97'_AWQF6.VB04Bc%5EMx9u%23%3F%24FL%40bVytU%40wc-W0P!H%22mtUMZZ%2CcFUKZi%3EIxyB%2CcMWEW", true, -518), "G.%40%3FgLEvN%60%2FEwL0u9mk%5B4u%2C%26zjb%2Bvdhv83%22MmJWsXz!t7%2BNI*tL8a%2Cd%25U%7CVBI*%22%2F%2F%608z*%20%2F%3Er%7DMNv%608%22%2Cy%2C");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput(".%3C9967DU%7Cf%2C%22%5CW%22P6Vx%3FYL9-s%2Fy%406xaTo3PDc%7D%7C)sbaKmc%3EfwC%5CdqGd))%24V-Af%7CO%7CXn44z5EZ~Z.%5Cz82sal%3EN", true, 846), "%25300-.%3BLs%5D%23xSNxG-Mo6PC0%24j%26p7-oXKf*G%3BZts%20jYXBdZ5%5Dn%3AS%5Bh%3E%5B%20%20zM%248%5DsFsOe%2B%2Bq%2C%3CQuQ%25Sq%2F)jXc5E");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("_PHaPqx%C3%87Y%7CS%2B!%09%23a7dR%22e%C3%87t%7CTr.1%C3%91g-t%099bc3%0As%09%40%3D%7Ce%09h.%0A5%5C%25%5DQY%25%5BkuwPT8%23V8ea~%C3%87%C3%87vo%0ACn%3Di8%3D%3C%3A%220%2Bm%09HvB%0DB3H)", false, 69), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("QzU%7D%3CTN%2B~je.8%C3%91%26pK%2FOJQNY%60%C2%A9q5%3C%3AzVS0X%0A%C3%91%C2%A9!%2F%0A%20qPjUdWs%2F%22Ou%3E8AQd0c%60xQ%22%3Dda%7D%2F%0A%3DMxk%C3%87%3DA%2F%5Ei.2o9%C3%91bvt%3F%2BphNJB%23F!q%238%C3%87a94UZBqP2C%C3%91m4!z2c(go%7Bw9)0", false, 794), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("Q%3DI~0%3BR~)%263~Hz%60_%3CG2PgR%2BPkB~GWwoO0y%26Be%5BMEKE%3BusU%2Fm%3C16)_9%3FvXr~g%40LUIM%7D%3FS%2C%5CNtq3_A%5BQv%5E%254qEJIF%5DZuf%3DDQe7keL%3C%7Cx2L-L9%5Drj%3F6%7B3vlX%60%60%5B%24H%7D%23JV%26%2B", true, 592), "gS_5FQh5%3F%3CI5%5E1vuR%5DHf%7DhAf%22X5%5Dm.%26eF0%3CX%7Bqc%5Ba%5BQ%2C*kE%24RGL%3FuOU-n)5%7DVbk_c4UiBrd%2B(IuWqg-t%3BJ(%5B%60_%5Csp%2C%7CSZg%7BM%22%7BbR3%2FHbCbOs)!UL2I-%23nvvq%3A%5E49%60l%3CA");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("h%5DC%2Bq%3A%2CtT%2C%20d%3AMy%239Pj%5Dp%3B8%3BKN6m%24I3lA%23ROSe4z7d%2FFbZ%7B*fOjy%60Q%7CAL%5CnfimqI%60%3Cd!W%3D%3E%3DC%25c%2BC%5DeP0%25%3AY%7D1mLTAy~F.%3A%3FL%7D%5CY*%3BswFu%7D", true, 2422), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("lTs%60Nh.G6z%60V%60C%7BQ_%2Fp%3C3itY%7C*%3EE%2BA%2B%3E6ShYr%22w1v%3B%7B", false, 610), "D%2CK8%26%40e~mR8.8zS)7fHsjAL1Tau%7Cbxbum%2B%401JYOhNrS");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("l%60YG%5C1Z%23d%3DHdl9%24nh%C3%9106%7D4.EGZ%225%2Cy%0De%5CYR0~dm%2F~%2B%60DH%3C%2FI'n%C3%87VTwrj1%7B%7CiI%3B", true, -881), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%3F0Vv%5B_uDgTgKc%5BR%3A6OkI%20l%2B~s.2%7C~%20PQ)nPQ*%24r%24B%25%3FlM%22%3EHweCkJ*%26S!5%3Bi%2CK", false, -2), "A2Xx%5DawFiViMe%5DT%3C8QmK%22n-!u04~!%22RS%2BpRS%2C%26t%26D'AnO%24%40JygEmL%2C(U%237%3Dk.M");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("s%5E%26!jFxlglpWpe%402%7C%24%5CIGT%5BNy%7B)J2!%22%22f%5C8xfV%40-%23ZvJ8%7C%22u2%7CFa8~RUT6I(%26N%7B%7CS%237%22%7BTmQ5xj3%3FP%3Dt1E_3V%5BjqrE6%40h!1%7CG%3B%5B%3DY%3ArQ~j", false, 670), "nY!%7BeAsgbgkRk%60%3B-w~WDBOVItv%24E-%7B%7C%7CaW3saQ%3B(%7DUqE3w%7Cp-wA%5C3yMPO1D%23!IvwN%7D2%7CvOhL0se.%3AK8o%2C%40Z.QVelm%401%3Bc%7B%2CwB6V8T5mLye");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("L%09%3FNa-Z%22cT%5D(%3Evms%09%5DvT%2B%C3%87)%0A%5D%C2%A9WWOghn%24%C2%A9%C2%A9Z4h%5D%25-w!)9IyaK", false, -414), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("Ld%C3%87_%09.f.%C3%91C%0Dx%0A%3EKao%09K_j%5DcJB%0D%5EXj%7D%2By%0A%22%C2%A9%C3%87Q%C3%91%3F", false, -1013), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%26uPCT%22A6%3EGqr!%5BS%5C%C3%91aw48QB%3A5yhY%22-%3EYOCX%09D'g%23%3C3F7ta!*X4w%C3%91%5E'%3C'%C2%A9g%7C%3B(bn'yFc%600tI%60)%3Cs%2BPt4NVtO'c%0Dz1%40*Ecd5Q%2C*U9%2B%0Dc6s%60Mz9", false, 865), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("k%22ga1%C3%87R%7B%40v%3CZ5L%3FP%5By%3Ap%25n%C3%91h%3FP%C2%A9o9rM%3E%7DDo1Y-B%2BhqRW%20%09nV4", true, 966), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("X~%5Dl2)%C3%91%3AjpLOT%C3%91d!v%C2%A9o%3D*%5Drx0VcRoUS%3F!McqmX%20%23%40T4%3A%22k%3C3Pv%2C%3BuvjV", false, 35), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("xmph~1E(k%3DY%2C%2FU%400rW%3F%3A%2C%5C%5D_Mc6q%23R%5BmQX%5EMvTcI.E%23", true, 2630), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%3AV%7CaO_zX%22muai6z5WXB%2C6.F%7D%5CT9Euhyr.9M%5D%5ER%60siR6ZmT_%7D%5B%5DCIk%5D%5BR%7CCiN9D%24%5B!jbBN%5BxL%7B%7C%3CR%7C%7DZ(UABl!%3B%3BoBHbADIK'%20%2CuS1%22p%3C%40J.51%5Bxg", true, 4566), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("DN3%5ENzrS3vYFjaXYkFb%25%3EA%5B13kzrc(%7Bu6dHQr%7DrM%5E%24VWrE!%26S'F%2B%5CRAa%5EWR%5E0%0D3%408o4ko6B%3C4%256rk1%7B%C3%91%40a%20'%3Ek1JFe5kH9%7Dk%0A%3AbknQ236NTKA4_9", false, -125), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("Mqr%3EwSmpu8%5BHe%7B%2Cn%2FG'%2CR54oo2bcN%24*jbC%3EuxPLd%22!%7Da%3DF3euv~%3CvA%2BW1j%23x%2F6a%24%2CF%25", false, 503), "1UV%22%5B7QTY%7B%3F%2CI_oRr%2Bjo6xwSSuFG2gmNF'%22Y%5C40HedaE!*vIYZb%20Z%25n%3BtNf%5CryEgo*h");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%3Fm%2FaNUC%23%3CnVJ5V%3E%5BT%239x9j3Jq%60ddpxG2fku8h~4wsrtdlc%20X-Sk%24A5(%7D%2BaTWc5%3FI%3Cm%26*L2B3p6M%2Fo%2C7n%3Bem%5EtB%2C!IRerd%3Fym6%5C%5Bw%2C%26'fe%5E%3AaT", true, -7777), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("j43)r2x%2CQ_p1!r~H%7BK%228%2C%3FnMlv%2C57x%20HXsU3%60yD%40iD%26.%22C.X2t66IF)Q7L%3CS_%2CXemZ6bRN)%5C%24Uo%25*j6%25KL%7CP%5EY%7D7%5B%2C%20rAS1y87)P-Zuf0%237OzBdnl(%2B%2CRos%22", false, 571), "i32(q1w%2BP%5Eo0%20q%7DGzJ!7%2B%3EmLku%2B46w~GWrT2_xC%3FhC%25-!B-W1s55HE(P6K%3BR%5E%2BWdlY5aQM(%5B%23Tn%24)i5%24JK%7BO%5DX%7C6Z%2B~q%40R0x76(O%2CYte%2F%226NyAcmk'*%2BQnr!");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("B%5B0IG7i4i(%26%23S_%5BAuvfPT%2CAQ%3E69M%5Dw%7DzJLyDz%5B~!C", true, 519), "n(%5Cusc6%606TRO%20%2C(mBC3%7C!Xm%7Djbey*DJGvxFpG(KMo");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%09%3FYCK868%26BK3%25rkG%C3%87%C3%87%C3%91%24M%3Cc%40FE%C3%91%22'6PmCaiHej5JI%23hn-tBS53G3ML%5CJV%24o%3AWsE%3DpLZ%5D%26O%3AM%7D%2F6WsTDt%5B", false, 407), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("HWXlGw_MX1.o~%24oGENIcP-CNGx'U%2FpR%7Dc~i07rx%24d4%5DpS%40*%7BshLeu%3B%5E%3BMI%7DEW%5DDvywK%22MXhhUu%20hR9%2B_HhxrR_%23z%60I%3EJoj(%5DTp5jSxy%60C%22.", false, 4485), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("XZQHdDHXg%5EvuYgE'Pum%3DN3HQ-%3AS%2BgmdzNH%26%3Av%2B2K%7Dwi%5BTGy-%5E6h%23Dm%3A%3Aox%5By%24-BJAh%40uXfGncJ%2C%3Ca-%3E%3E%7Cy)%25T", false, -370), "NPG%3EZ%3A%3EN%5DTlkO%5D%3B%7CFkc3D)%3EG%230I!%5DcZpD%3E%7B0l!(Asm_QJ%3Do%23T%2C%5Ex%3Ac00enQoy%238%407%5E6kN%5C%3DdY%40%222W%2344ro~zJ");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%09z'%3D%7B8Uz7H%5D%5E%7Bb%7Cd(%09va%5C%3D%7DUk%2C%7B.%C2%A9Pcn6%2Frif%3DmvN(QuRk%3Ak*'%23kj%C3%87%2C%3Bh%2F%09%2CsXLZ%7CgY2eZ%C2%A9b%3C*%7B%5E%26dZr%3F%60X%C3%91%0AJc%3Eq%3CJjyXKH%22%269%7BKDz%7C%24%C3%87ZBo)", false, -941), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%5BMHwg0O%3DJ%40pA%23M%20%0D%0Av7Ca%22q%3FN%5Dq%2F8%C3%91u%3Fibn%C3%91v%C3%91%23L%C3%91bp%5B%C2%A9%26h%C3%91X9%5C%2B.%3C%23xIb%C2%A98bAE%2B~%C3%91-0D!%0A%3ESB*g%3F%5CF%60%5C'_%26%C2%A9%7DbM%7D%C3%87maq%2BNZ8%26%24ft%26It%C2%A9t%5B%7Cg6%22k%2231%C2%A9y1N%C3%912%60u%5C%5B%25M", true, -473), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("d'%3BD%5D%26iHout%7Cc%228%3AAXJfHDvyQpLM0%3B%3A%60(%25.gbz8JeD3%5BMg1m6'(%7C%7D%2Bp", true, -734), "~AU%5Ew%40%24b*0%2F7%7D%3CRT%5Brd!b%5E14k%2BfgJUTzB%3FH%22%7C5Rd%20%5EMug%22K(PAB78E%2B");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%7DnPCoEO%248%3A%3DhMhlAPzOYR5mFl3%60I-%5EU2d%22c%5D%3E%20G~Zo%60pHS%7Do%22QIZ%5CQ%2FTN(grD%3Frq3Qo%2661K%26q%22%3Dd)%26xob%223nwq%22Tz%23s%5Dz%2Bw%7B%3BG%5B2!M%2CFsx-TyPMMp%5Bz_K9%2B0Tmj%5Bi%2FG", false, -1023), "gX%3A-Y%2F9m%22%24'R7RV%2B%3Ad9C%3C~W0V%7CJ3vH%3F%7BNkMG(i1hDYJZ2%3DgYk%3B3DF%3Bx%3E8qQ%5C.)%5C%5B%7C%3BYo%20z5o%5Bk'NrobYLk%7CXa%5Bk%3Edl%5DGdtae%251E%7Bj7u0%5Dbv%3Ec%3A77ZEdI5%23ty%3EWTESx1");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%C3%875%0A)8%3EFGh%09Wr3dm%0Ay%20uk%0A%C3%91%C3%91%C3%87%2F%C3%91%7CoG82cdv%09QDP%0A", false, -899), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%24a3A%60%7CJJ%3Bd%3FB1Miq*ClH1%20l%2C%2FN%40*EcOfG%2BdNd(%5C5t%3CPkON5", true, 873), "6sESr%2F%5C%5CMvQTC_%7B%24%3CU~ZC2~%3EA%60R%3CWuaxY%3Dv%60v%3AnG'Nb%7Da%60G");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%23%3Ca%3CM!7%3De%5D!zN%7B_NF%22B3G*%26l9%3Bi.%206Ee%3DRQ%7Dq.%3BZ%7Cg7J%5BSd8o6U*3%5D.T%201RYt~%3C%25%23%7D", true, 5659), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("RRAu%25dSYgw%2B.2%5EkWDrRHFEN%7Dv%09%2BLSqACK49%7B%23%2FqYPN*%3D%3AcTn%5Cz%3B%26%0AdVWohRthHCoT5u%3B%3C%25%0A%24t67d3OjbL_~kt!8%7Bv%0D0%0APO'b*!pG~qMUc3G", false, 437), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("0%5EHH_-%5Dl%2Fg%3B2(n%0AP%7Bg%5E%2Bi%3Dm%3As%5B2%C2%A93j%7Bg%5DaG5E%60e0i%5CW%7B7%40d.q%5E0%09iS%20%22%2CA%20o3SIb%0A%3Cqe%3Dk%7CJZxaXX%3BWhL%5E%5C%244ta.%2BX%40JjW%60%7C%C3%91%3D~9%3Ak*m%09MsT%C2%A9%099R", true, -334), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("eYTzDdeAC.%7D_%23%C3%91R.eiB1%C2%A9qteM%7B2y%40%0ATPBx%0D%26n%3E%C3%87I%239c%2Br%3B%7CYsUJ%60hC*mr%20%20%20%7C%7D%C2%A9qVMA%3E7CG%25%23", true, -366), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("P%09Ok%7B%C2%A9%0DP%3FW%C3%87%22G%0A%0A%C2%A9Vv%24D%40f%2B%3B~(0QoN%60G%0A%0D%3A3%3AP%7C", false, 8), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("Z%7D%5E%241s%26BbbFvS)rk5XhKiO(XQW6m)3elyU~%2Fql%5D%2COVR%3Eng%26bfGx%2C5%5D%23'8ql%2CZ%20%3C", true, -1731), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("d%3Ab%25S12p%2BY-Gg%7Dta%3E'x%40G%C3%87gDJ%20epPG%3Eu%3FN%C2%A9x%2FvO4Ph%7Br%3D%7B%2F(UzLML%2F%60g%7CS7%5EaaV%2C4jLk%3BLf%0A%3D%20%5E%3B_PdC1prTu%5DX'ZuvOUI%5DY%3CP.0D%2CEG%20", true, 582), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%7D3i1%3A%2BCYINaKswm%C3%87%22%20%2B*%7B0sX%238T%5DAc%C2%A9dmHvs%3C%7DH'WT%3B%2B%2Fe%40LLO%C3%919Mtle%C3%879IJdSn%5D%C3%87.%5EU%5E9IV%7B(RRlF%C2%A92mymL%24%3F%24%C3%91I%5DUQljf-", false, -120), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("x%60%7D3LB(gz~e%3E%7BylYKQ2~V%3El%40CW9ohAzk*PBcQZOVC~%3Cl%5C%7DL%5D%5C(q6j.y9Jy%3CAgP%7C3Pe%22rIJt%2F%3E%3De(Wa*", false, -10115), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("brS%7B2IVs%3EDhhExb%22%3ED!ejSH7%605K84xtBf*F2E%5E(%25Ldn6%3A%5D)-%2F9C%3E61", true, 776), "r%23c%2CBYf%24NTxxU)r2NT1uzcXGpE%5BHD)%25Rv%3AVBUn85%5Ct~FJm9%3D%3FISNFA");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%3Fz%3EUa5%7Dg5dF-U6kM%40g6%3B%22a%3DUr%2COC'boj%40OD%5B%200%24kRR2-E", false, 682), ".i-DP%24lV%24S5%7BD%25Z%3C%2FV%25*pP%2CDaz%3E2uQ%5EY%2F%3E3Jn~rZAA!%7B4");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("xy%7DVe%3BQ%5BxI.xPEm%3CFHz9%5E%60sWU-%23h6%25%3BwH%3FC1Lb6X%7CEW%3BUE%5CL%22TruJy!vW8gY*M-SDF.si%5Cq8!Ibhqkv6r5f%3B%24ExY", false, 255), "78%3Ct%24Yoy7gL7nc%2CZdf9W%7C~2usKA'TCY6f%5DaOj!Tv%3BcuYsczj%40r14h8%3F5uV%26wHkKqbdL2(z0V%3Fg!'0*5T1S%25YBc7w");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("J%0AXg%20J3uSgl%5BZ%0D1pedju%C3%87K0Q%23ax%5D%C3%87t)Ex4%7D%401(%C3%87bQ162u1bPd%5BU%2F%C3%87", false, -375), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%3C%7CxO%3A%5B!sjJZDA%5D3NXsRO%24ho%5E%5C0%7D%40%268m05qJHp*fs%5D%7D7z%3Ev1%5C%22q3R%3F.-%7B5%3AknJ", true, 8535), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%3B%3A%40TFfE6V4%22jlr9T%3Fw%23Y_flx%26OfO%5DEv-c0D", false, 7614), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%5E%7CZl.9a!%5DKUL%7B8Hy%5C%23oKw%26hoT%5B(4vA%5B%3Bd1!D%5Bc%2BU%5Dn'%25Y%5ELM.b4%5Bc5D!6%233T%2C%3D", false, 2500), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("CQS%3Cun%2F%23n%22%20%5ClxL%C3%87%2F5c32%40%3Bq%09%C2%A94%7Dt6JXF%5ER%3C7MMroP%5D%3Aj!l%0A1302V%241w%C3%877S%40s%C3%91s4%5B%26%22%2BFo%C3%91rBK%C3%91vjzh%7D%24C%C2%A9%3D%2C%0A%C2%A9FiXN%09)%0D%0Aja", true, 694), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("K%23)2JW71QL%3D%5E%2CD2AW6ny1%40%2B_%3A%26zfjt.8%7BC%7DZK%5B%22Qyigo%22'p84DSV", true, 224), "mEKTlyYSsn_!NfTcyX1%3CSbM%22%5CH%3D)-7PZ%3Ee%40%7Cm%7DDs%3C%2C*2DI3ZVfux");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%3Dnu-q8*%5DdQI%3CQqQ%7B%2Fl%7C9z%7C_Y%2BK%23e%246R(iw%5B%24%5DOu-6CC%5B7u4%7BSvIelo~%40z%3B94TPsD%7CKEYDES%3FxB%23%5BK%3E%2FBjs%5ECoB%2CH%5Ba!5", true, 3324), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("rmr%C3%91jG%20%3DqU8D%C2%A9ax%C2%A9%20Kl6y8Rv'e_m%2Ccrkb%5C9w", false, 633), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("5~HucFgyb%24m.'R%3B%22K%5E%2CtO%3Aae%60lY%7B%7DME27HvjeTIGBi%7DTY%40dI)%5ES(eRtXLXG%3CBY6C%3FPe%25%26PV%5CX8%5EDn_~sC'aA%7B(G%2Cjc%25FjjLzZly5zS%2B%5B536%3AA~qg7S5%22", false, -3572), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("wz%3C%5Db%7DD'Y%2F-%5BLZBv!%24%3DsN(i%23hG-U%20%7DU1%5C%24x1%2BLMm%5EE-3%5E1q'_'(eV%3EOVIr%5DGzDl~O_%3C%22%40WP%5DleX(S3%3Bd0*N.0y1q_I%2B1)%7Dms_e%40YB3%24QepO%3E%25YWO%2F)%24-xL%7C_%24lvg%60Gx", true, -345), "%3B%3E_!%26AgJ%7CRP~o%7De%3ADG%607qK-F%2CjPxCAxT%20G%3CTNop1%22hPV%22T5J%23JK)yaryl6!j%3Eg0Br%23_Eczs!0)%7BKvV%5E(SMqQS%3DT5%23lNTLA17%23)c%7CeVGt)4raH%7CzrRLGP%3Co%40%23G0%3A%2B%24j%3C");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("xul*j%26II)s.9I*D.CzI%25MucU%5C%2B%3Ed)xaKu*xpoBLQe%7B%3Dc%23sz%5E-%252.!%250m%5B7l%24F%3E!L2%2Fk%7C%7D%5D6%25%20)A%3C%5D2", true, -1326), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("nHM%23foRmZxke%2B_TI%26Y~Av'I5ZU%7DZ%3Exa*%24%7D%60S)(%3E", true, 1019), "T.3hLU8S%40%5EQKpE%3A%2Fk%3Fd'%5Cl%2Fz%40%3Bc%40%24%5EGoicF9nm%24");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("I2O1-c%25so_58l%5Eh%23!i%3EGkKD%5E%2CaxFL)gPs%5Ba%209%3AjOcM%3C0%5E)kkBH%5BCDxn~!%2264m%2B%3B3%2Cw%7B.tAC8%25(*%3D%7Bm~%20N695hAQ%24VyL%3Frq%5D%5EY%3F%20%60AX(zr%3BXm%26%5DFNNIZRP0", true, 825), "%2Bs1rnEfUQAvyN%40JdbK%20)M-%26%40mCZ(.jI2U%3DCaz%7BL1E%2F%7Dq%40jMM%24*%3D%25%26ZP%60bcwuOl%7CtmY%5DoV%23%25yfik~%5DO%60a0wzvJ%233e8%5B.!TS%3F%40%3B!aB%23%3Ai%5CT%7C%3AOg%3F(00%2B%3C42q");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("cCvi2Npa%2C5*Ed%603uq0jU%5DG%5B(%3Fg5GXe%5EBc%5D", false, 3156), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("A-~%22%5C%3Bn%60OVF%60_Xv%3B%5DC%2FEt4%3Ca%3C4CJi5%23gBaCe%3DcpRH%3Eab'U%24.%5CgTFX)%24%3Da%2BqBQV)p%5D%7DL45e%3B%25H%3D%3FE%7C%7Cp%25%40M1%5EN%2F%24m'0310E%2Cf*BrZ-.EW!%7B%7D", false, 401), "%2CwilG%26YK%3AA1KJCa%26H.y0_~'L'~.5T%20mR-L.P(N%5B%3D3)LMq%40nxGR%3F1Csn(Lu%5C-%3CAs%5BHh7~%20P%26o3(*0gg%5Bo%2B8%7BI9ynXqz%7D%7Bz0vQt-%5DEwx0Bkfh");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("u%C3%87%3Dh%09Oi%25LMT%5D%5EX!Q%3F%2023!XG%C3%87%7Bm!%3E.s9%23%5D5%7B-DddUD!jg%C2%A9%2CGt3i*%3AC%7D%3Ci%2F%204", true, -774), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("m*%2C%20%7DvV%26X%22T%7C*l%60M%7DXT2z%3D%2BB).wk%2CiWE%5DWB%5DI!sMemH%3CUD%3BRnY2yw%3EOOlr4%25%5Er1MmYe8%5BQ10N%3Ezfx!%5D%5C4A-W", true, -463), "y68%2C*%23b2d.%60)6xlY*d%60%3E'I7N5%3A%24w8ucQicNiU-%20YqyTHaPG%5Eze%3E%26%24J%5B%5Bx~%401j~%3DYyeqDg%5D%3D%3CZJ'r%25-ih%40M9c");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%7D5'WLI%5D79vFGxU%7B.T%3Bl%C3%91lPO8%2COiPpS5Cn-h*7Ep%5C'6SV%2B0Ul7%3Cp%7CbnuJ4a6FzQ.X%5CAx0%5Es%5CpfE9%60%5DMLcie_%22f74XB%C3%91NDXl%5Dt%2B%2025j%5Cdw7q9", false, 639), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("r%5Ef%2CqiNf--%23oGD%5B%5E%3Ab~7URE%3F%5Evx~pME-%5C4JZwnMLTNwtkN0M%3C%260hQf~l5MOy%26ScuVRBkaY%242r5AMO*KT-K(1nl%22nH", false, 92), "uai%2FtlQi00%26rJG%5Ea%3De%22%3AXUHBay%7B%22sPH0_7M%5DzqPOWQzwnQ3P%3F)3kTi%22o8PR%7C)VfxYUEnd%5C'5u8DPR-NW0N%2B4qo%25qK");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("R%3Bk'%23uD0i%60%26%3Fk1A%22b*bIA%2F5OSCS%2B%7D%5EH'tFK%3C%24POwK'zlsj%5CRuIlv%265r%5B!oJG%23L%22E9%20se%3B%5D9l%2B%23%7B%5E%3EIwrz!Rc", true, 5870), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%23%7Dhok2%3E%7CGe%3F%5Bd.2we8H%3AUyU%24%2Cri0nUWm%60%3EI2%5E%23w.%22TaTf%20oO%5B~z'kWQR%7D!m8b%24c(gc9TbG*z02%7D.uovPX5v%23ararLgl", true, 5927), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%5Bh%7D56w%2FZSb%3F%20e-Y%7BCOwp%2C(%7C.y3%2BR-%5CqD%3AScw%25vDM%22(w%20fe%60e~-%2CM'5sJ_%3ATV8%5C%5CJS%3E%3D%20%2B%7B01s_h1%26%400bDZlR%5E6SI%60Fol2J3%3Dr%25g5%7DXDX", true, -319), "9F%5BrsUl81%40%7C%5DCj7Y!-UNieZkWph0j%3AO%22w1AUbT%22%2B_eU%5DDC%3EC%5Cji%2BdrQ(%3Dw24u%3A%3A(1%7Bz%5DhYmnQ%3DFnc%7Dm%40%228J0%3Cs1'%3E%24MJo(pzPbEr%5B6%226");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("z%2C%5E%25~(%3Co*yQ%3A%3E%25yvk%7B%265%3COi8p%3B5zl3%220o%7D24EVv3NZCfZw7%5E8VSPV6%3B%7Bjp%7D%3Dk%5D%7DPg%3Cm%24fh2%2F(D*QS1IkrM%40%22R", true, -2420), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("In%228ca%60WBdq'Tan!LIXzTz-DC0X%2C)o-l.KS%7Dlz%7Dq%3Dq%7B%26%7C-1D(M%3A%25~%40(", true, -633), "i%2FBX%24%22!wb%252Gt%22%2FAlix%3Bt%3BMdcPxLI0M-Nks%3E-%3B%3E2%5D2%3CF%3DMQdHmZE%3F%60H");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("*h!Y(rfjM%7BdEnT%7DhF9%5D%60mR%5E%20ngH%3C%3EU%23%2CD%7CjT4%2F8g5xv'hM3r3zh3ev%2B%5EI%3FV%5E*z!NdnE'%2C4%3Bp4-tA%2FksN%247j'e", true, -1111), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("KG%603m%5Bz%7C-WpJmVTs%3F!)o9h2q45IJT%240%3EkClKY(He0bEE%2FQ%2F)GhS8q%60%3FgJ%5D1l%2F5r%25Dx%244%5D%22(2s_4*4j%3EK%3E9cI7%60e%7CN4AIN8%5EA%5DTJb%60)6%22", false, 97), "IE%5E1kYxz%2BUnHkTRq%3D~'m7f0o23GHR%22.%3CiAjIW%26Fc.%60CC-O-'EfQ6o%5E%3DeH%5B%2Fj-3p%23Bv%222%5B%20%260q%5D2(2h%3CI%3C7aG5%5EczL2%3FGL6%5C%3F%5BRH%60%5E'4%20");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("WS.M%7Dch6hJ(2%3CO%7C%5E%7B(msVYv%2B%7BhFhxB%3FG%5Dm%26yky.5susz%2FMa05%5D%23%7C6'1kO%7BwU%7DpS72A4qXTrR", false, -758), "UQ%2CK%7Baf4fH%260%3AMz%5Cy%26kqTWt)yfDfv%40%3DE%5Bk%24wiw%2C3qsqx-K_.3%5B!z4%25%2FiMyuS%7BnQ50%3F2oVRpP");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("5J-0Y0)y1Fp0%3DQ%5ExiE%22%22It%5C%26fS%2C)%7CMf_j%25fCbaG-TN6!%60U", false, -3347), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("~1R9a%5DE'%40*a%3B%5EQ%245fm%3F%602w%2B%3F%243%23qc9%7D%5DRPu3HX%25%3AUs5tp_n19mYpB2%5C!fn5e%7B", false, -711), "M_!g0%2CsUnX0i-%20Rc5%3Cm%2F%60FYmRaQ%402gL%2C!~Dav'Sh%24BcC%3F.%3D_g%3C(%3Fp%60%2BO5%3Dc4J");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%3Aa%23%23hq(%3CA(pk%3C-PKqj%2Bb%5E_e!%3F%207%26IJNrDE2BumOm)Nc%2FDV9FCJmJF1%3Bvm%243h%2C4%2CZ1%7D%24)!g7-V%3Bi%5ET%20%401%20z2*V~twKG4u%25y%24%2Br(%25*eE%60%5BD4%7Bah7R%2BdQQjOi%206-", false, -621), "m5VV%3CE%5Bot%5BD%3Fo%60%24~E%3E%5E6239TrSjY%7C%7D%22FwxeuIA%23A%5C%227bw*lyv%7DA%7DydnJAWf%3C_g_.dQW%5CT%3Bj%60*n%3D2(SsdSNe%5D*RHK~zgIXMW%5EF%5BX%5D9x4%2FwgO5%3Cj%26%5E8%25%25%3E%23%3DSi%60");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("nNsCi8%5E%24'%0Dl8Wa%3A*8*G%40D4%C3%87v3payTb%09%7Db%2C%C2%A9_%3B4s4.DiHab%09%22.6Lu%0Dj%0AuO51W%3A1%2B%C2%A9%24%09%60.%23pV%C3%912fIzk%0A%3C%26'", true, 339), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("_vC%5El0%26%3BeZ50d9%22%5Euc6A.9p%7DG%3C%5C%7C%26qkFv~%5BIq~4tX%3Bm4GP%2B1TIOt-%2F%5CV%3EOl%7D", true, 4595), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("Q%5D%2C~Rf%5C%7B.!%2089sy%5DQnBItwm%60jTNhfZ%26)gh%5C%C2%A9%5CH~%23%3B%7C-j%0Aov%7DFSpL", false, 240), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("!!7q5FrD%2FA%5B%25I%5DD%2BR*LPPe2d%24L~r%5C3Fl%40p%23%3ApiYLBtl)Jg%5C9dck%2F%3B99%3F~%26%7B'T7%7DV4%5B0qTN0hX%24Q%226o", true, -2500), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%7C%7D%5CZ%3Cf%2Cn%3BMas9%5DM2%40%2C1.W%7D%3Atq%2B%3AHP2j%3F9%3D0Vc%7C%3Fttc%3Br)%23%2Cy%3A%60YF*LZ(5%2B%5DKr5aQ%5Eme%5B%251h", true, 4037), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("*e%25%246nh%2Bq%2FLc%40)DO%20aeHe*%60T5b%5B25%5DI6%5E3kOSu%24%40d%3AYrK%2FL", false, 4210), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("1EA%40%3BjA6%5B%24bKMl!!!%22ZRwaNH%7DCxYcg75-b", false, -7455), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("O8z'%23My2%0D%09rlF!b%7C%0D%26v%09hWbRa(%7D.L(8%22%09-6%3F0csCLsSqR7s%7DvM%2B%C3%87U%0A%09U(l%09%7Bz.%5E9%7D%5EJh%C2%A9%0D%0A%7D%3FOz%5DmE%C3%87%7C.A'%0A%26j0%3BwZ%3D!%3FfIT%25IRBX%C3%87q7zD%0D3~O%60qd", false, 219), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("u.%5DR%5DyHhPa!%5E%3Dk-%3DDW4vOCMt%5EAYk%22zj2-%3A%3CWSv_jcIVuMAuWj1r%5DSkp%3CZ%3Ef2G%5B%2B%5BSA%225%5B6hk60j%5Bz.2rAy%7CS1yvP8Pb%5B%5B%23RT4x%3A%22%263oV9tuN%3C'bhe", true, 1617), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("gz%0DGTp%7B%3C%2C5%C3%876Zw%2B%0D%0DG%24wOQ%0A*DhgV%097%C3%87s%7DSWg7arkk%C2%A9%7D%3D%C3%91_BKVD0%5Cg%0DOv%5B%C3%87(%C3%91V2I'6B)%26*1", false, 717), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("S%3DkM9%3A%20s%251oUZbiIGjhggdagcUEki9HyK0%3CjQ~vAJ%409.Y7E%2F*g9r", false, -4453), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("7%C3%87M%5B(!5sA%C2%A9Yv%23%C3%87uC%2C%7CF1eW4a%C3%916*x*%C2%A92G)%C3%87%26%0A.ss%5D%C2%A9%C2%A9%24m%2B%C3%87%0DPuTrR", false, 715), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%5EidrwMmx%60x%2Cj%3E%3CZiPe%0Ap4!%25_o%3F4-%C3%877%23%22H%C3%91zQv0%3C%2BJKD%C2%A9pc%7C0%3B7Kz%3F%23X%3Ey3EV%24CA%C3%916Or%0D%3A2~%0Df", true, -98), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("F%220T)%40!I%2Cy011%5ErNx%3Co!E-U%3AQ(%3F8Ml*%3FnyD%3DF%2CszdG%2C%24yns8Bi%3D665Cf", true, -4306), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("L%3F%24NJ%2BHCzIsgurb5_u%2B_!%5Byk3)Up)eB9D*bNym%26%25%23w%40l%25K%60NPs%5DU')5%204", false, 1343), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("o%0AiDEX%7B%0Aq*%40PmKM%0DB%3C*52LG%0D2%2B%20%0D%3A%3Dkt%255!8N%0D0mxecIo%23.n%09%23h%7B)CEyzPHy*_sH%3F3%09DY%2F%C3%87m%60.XTe9f69G%C2%A9yGHI%5DcI(fde%C3%877%24aUXd%24%25%0D%0Dq%25s%2539%264OQ", true, -831), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("DmHZuPqf%5Cc%609%24Wk%7Bnm(4E1O%40%5E%3A)%3F*F%23dY3%7D(i.)bEB%60%60M%7CHKA%5B0mMp%24~W%40R1%20%3BZ%7Bie%604Bn%3C%5D1%5D6t%5E*GU0ew%2BkvgV'!%22Uc%235pHmA%2B%23b9x%60kYB_%60v%24T", false, -3104), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("tzRU9z~4%20%2F%3F%3B'%3AN%2Bf%3EJf%25kCy%23TBQ5bt%23%3Fk0%2F7j7%7CqKcv%22I~vw_s-'sd%3B1loGt*AQ%5BQAb%40n", true, 6639), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%60%26b-%5C.lj6OxKCsp%5Ec_G)%40'o_cQ9Z(vo%20%242%22%24P%7CV~%5DYR%24uP%5DyO9%3FF1JRZ(5puH%5D%25~E2y5lVp%5B%22%2F-0q%5Cm", true, -8277), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("3%3CA6n%26Ngyz%20%3CK08%24a2%3D'z%7C)x8f%22qeaEnY9%23%09G0%22y%2C%5E%40%09%3B%3E%20J'yu%2B_%3F%242%24*q5xUv7Ot%26%23tbdu%3BAb%09P%7BO%5CGvf%C3%87vj%2F4X%7Cp8_%7D80%0A%3AYM(ZMhdQ%C3%91(s%3A_Pv5'%25%7BO'1%0A", true, -869), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("rv%3C%24_l%3D%24uA*%26~%3BM%7B%23%23%602YuC%7C5'Dl%26hFhaoM%5B%7BPMsjI(", false, 2863), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("n%2Cq5%40%2C~TCEON-%3E-cP%3Es%3FdxkZ%40)%7BoMiz%25n5%2CfHCV%5DmSvBAp%7DL%3AQ*%7C'%5EYws!i*86u%7Bd1!Sy%7CTZFQ", true, -604), "LiOr%7Di%5C2!%23-%2Cj%7BjA.%7BQ%7CBVI8%7DfYM%2BGXbLriD%26!4%3BK1T%20~N%5B*w%2FgZd%3C7UQ%5EGgusSYBn%5E1WZ28%24%2F");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("*66%20XYvV%23F%20bf%26t%40%3EMjVpx1oQ0lx%5E%5Ei%406SuUu%2B4%7Bv%2C%3AW%60'zwD%5CliPKO%2Bm~%2B%40%24%5E%5E'tY%2B%5EOx(T3%7C4j'f%3EhTz%2Br55yhe%22XvQ%20d%3Fq*l(q85%25FqQ*gYSU%7B", true, -9075), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("j%0D%3Ep%09CIJT%2C~FngO%5B9m%C3%91%0A%5Bj%3BxSzc6Ji%C2%A9%25", true, 95), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("~%24fqA%3BMd7tSL*WjeRaucI%2B6%24)*%20%3AyvJ%3A%25bE8%3DcI%3FoeM%5D%3EM%2B%3A%3CoY-Adq", true, 760), "~%24fqA%3BMd7tSL*WjeRaucI%2B6%24)*%20%3AyvJ%3A%25bE8%3DcI%3FoeM%5D%3EM%2B%3A%3CoY-Adq");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("rKI4.%25Z%23Qp*Ht8*Bef%5CyX8%C2%A9%3Dl-.%09%20.8D%23%2B5tz%3A%26Xl%20xFTW%7DW-%C2%A90(%3Co~8q%40l.B%23B%C2%A9GPX5%26'%3E", true, 1009), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("OFWz52yGYEMvAG~'%5E%7CsG%40e7nUw%22ut3M7m%40Y%5E6%2C%20i(%208si%3F%3C%C3%87%3A%3ClS%0A%09fFpU07", true, -545), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("2%3D%226G%5B%24S)3C48XA%23%3D%2FCOpE%26jjj7z%3AOCR%22V%23rpk%24DI.o_%2CAr%7Bt%3FX%5Cs0x9JF6h99%7D%5CtK%3B%7D%3DcVg%7Du%3FY%7B'Kq7pb%3F3T3TfXp'Fxo-WlFu~DugE%7Chx!%7BIa!SG7", false, 3594), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("g4Pq1ZHvuj%3C6%40W1r%3C6S2%3F48%3E%7Bg%7BC%7B%3E8%24~%5D%5B()iEgT", false, -752), "_%2CHi)R%40nmb4.8O)j4.K*7%2C06s_s%3Bs60%7BvUS%20!a%3D_L");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%40b'Is%3DY.%2C8wA3OK%5E!i%40%3EC3O6EH~Dt%5C3z%26%3DAl-qp%60%24%5D_Mg5b7hUaQXT2yo-i%40U%2CrVu%3CdRFt%5E%3CQ%5D%22.vTKYK_%3AboG1%3Fez7%2F_%2CPi%227e%7C'(DVBDl%232nUd%20eaOt", true, 321), "d'Km8a%7DRP%5C%3CeWso%23E.dbgWsZilCh9!W%3FJae1Q65%25H%22%24q%2CY'%5B-y%26u%7CxV%3E4Q.dyP7z%3A%60)vj9%23%60u%22FR%3Bxo%7Do%24%5E'4kUc*%3F%5BS%24Pt.F%5B*AKLhzfh1GV3y)D*%26s9");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("7KBk%3CN6x%5Erv(ozYZ%7B.t%22jvGa*q%20U%2F%3B%3A%3DuY%23%5D'JX77RRL%26%3E3%26Tf%261Zg%3D%3C%24dX~)%264%2C", true, -785), "%7D2)R%235%7C_EY%5DnVa%40Abt%5BhQ%5D.HpXf%3Cu%22!%24%5C%40iDm1%3F%7D%7D993l%25yl%3BMlwAN%24%23jK%3Feolzr");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("wy-X)%60%3CngdCaHz((%3CuA0%3CD%26(S%7BJ2BRlRCEeK%22", false, -3646), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%3D%0AVstQ%2B%C2%A9%C2%A9(%60%7B%5C%40R8f%09p*%0AI%20%3Ed%233%09%2BBlC6%7C%2455%23%60A2x5%7Dd%22E%2B%0D%09%40", false, -389), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("P!zX%C3%91Q%5B2c%C2%A9s6xD%24U%40!bxcy%5EV%2Fa%25%2F%7COuv-H%C3%87EHO5%3F4%0A%5B%5D%7D0vYtx%C3%915%5E%60%3A%09R.D%7BNM*vn%09g6%60Iudv%2BV-%40%23.%24gL*4%0D%2B%3E", false, -117), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("ErA%3B%60vA(Sr%5DFUNBixndcK%3CTU%25g%5BMKgpJ_wvSmO%5BS(3f2OnY%7Cs315'N)daQ%26dlnx%7B2H-qq26d7%3AEVBj%7BM%20%40_AJX", false, -708), "p%3Elf%2CBlS~%3E)q!ym5D%3A0%2Fvg%20!P3'xv3%3Cu%2BCB~9z'~S%5E2%5Dz%3A%25H%3F%5E%5C%60RyT0-%7CQ08%3ADG%5DsX%3D%3D%5Da0bep%22m6GxKk%2Blu%24");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("J%7CP1!%3C%C3%87S%09K%3DDPlh)Q%5C%25%0A.*%2BN7Ui%09d*%0A%25GoK*%2C%0Aa%3C%60N%0Dv%C3%91x%0DrC1%3F_%0A%0A%0DVgtd%C3%91E%5D%C3%91%3A", true, 975), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("Z%60P%24aPVd%25sry%26yc%2F%3C%2F%3B)0BRhV0v%3A%40%23k%26'.Ttp", true, 8399), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("b1o%3Cj%20Z%C3%91%60%5EK~N0%7C2Vu%3F)pFV%C3%91e8-%3A%60e_U%0D%C3%87R%60UBJt%5E%0ANN1V%C3%918%25%5EpmP%202gMY%09%20Vv%C2%A9*Id_%60RZ%C3%91%7B3P%C3%91VVF%3C%22%406%5E*l%22gG%5B4O7%22A~o~i%7D%3B%0DX.QP", true, -648), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("MR9%3CwW0%2CVm%60X%5E%2Crw%2CR%5CcA%2C'baZ%22Y%22v.(qY_Pd%230xL2y%40%40%7BX%3Ar%5BSA%2FI%20%7B%3EaB%3B%23d%24%20K%3FE%24y%2FC%7D.a.2b'X_uCpFc%26pH-NY0l", true, -2970), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("i%C3%91Q%09j%2Bs%24!%3FyXd48k%3Fp-%7D%C3%87)%224)R*1%0A21%60RBkw%09C4u5m%7D8%5D%3A%26MD%22%5B%09B%C3%87W%3CCO%C2%A9X%C3%874%09lN%09j%3D1%5C%2FG%C3%91K%22%5C-%3FA%09POm%3B%3ER%09*B%2Bp6LMxp.p%23", true, -146), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("Rxu%3BG%7D'MVPyL2~Sp2%20o%3EaBhh~UJXAt%3FWP7%7DzN%7CK%3C%20h)%2F%3CJ%20MRB", true, -2331), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("t%5D.%3A%238D4s%7D%3Ej5kT%7C%3E3eB%7D%3F%7C%5C5D5D3(v%2BK)%7Df%3E%23%22T83Q%7CzU0683.%7CVe.DqX%3C%2Bzbr%2FN%2C%7Da1Blqxap%2BIg", false, 8168), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("sd%7BMv%0D-%C3%917lSvv92%5CXh0dO'%5B8vJ%09YCONGz%24*9%2BEil3W_Kld%3F%C2%A9_0.xc%3C%5C%2CjQRgq%20tt(18HCamufsRPfIh82n%3F%2Bd%3D", false, 818), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("jW%60(g%3Bv%3BZfx%3B%7C(%24cYU%2Bag-Sx%5Cxc%3C%5EW%5Db9yV", false, -9641), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("Jrt~%3ABoaRisc~Zamu%7C8%7C%7DT%3A4%7Cvwh6C%3B)", true, -2973), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%24%3FqG~T%255%5D%5CT3vN8HxD%20ex8_5C3%20%7Cpyo%7B%22'E*IPMo%2C%3Ex%24)%7Dp%7De%20%60rbm.%23I1%5BMS%7B%3Aw%22!Uji*%3D0jX%3FQ47h%3FF8%40oWICYS%22zx%60%3AfPT'UmX6%3F-ynzn%3FG%6046vE%25%7C", false, -949), "%23%3EpF%7DS%244%5C%5BS2uM7GwC~dw7%5E4B2~%7Boxnz!%26D)HOLn%2B%3Dw%23(%7Co%7Cd~_qal-%22H0ZLRz9v!%20Tih)%3C%2FiW%3EP36g%3EE7%3FnVHBXR!yw_9eOS%26TlW5%3E%2Cxmym%3EF_35uD%24%7B");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput(")-UaS8%3Enxo0%3A!t0(7%3CuT%25BXp)*vx!!DZW%26%2CR%2B%24%23t%5Dp%3D%5CsQoaTGF*Xd%5DzopA2g%3B-BXBprMk%409%7CsS%3B%7DKCN!q%25%7CTQ%2BBuF'%3ALZ%5B5%2BPus%24%3B35%26(q%5Cn%3DeapzylsPr*", true, -988), "bf%2F%3B-qwHRIisZNiapuO.%5E%7B2JbcPRZZ%7D41_e%2Cd%5D%5CN7Jv6M%2BI%3B.!%20c2%3E7TIJzkAtf%7B2%7BJL'EyrVM-tW%25%7C(ZK%5EV.%2Bd%7BO%20%60s%2645nd*OM%5Dtln_aK6Hv%3F%3BJTSFM*Lc");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("7%5C%60a*yg!U%3C*0M6MNHWFhd%251s!%24Xgi%5DcC(SwwxJy!r5!baB%22~E(%7Cf~%60!%26tDX", true, -475), "7%5C%60a*yg!U%3C*0M6MNHWFhd%251s!%24Xgi%5DcC(SwwxJy!r5!baB%22~E(%7Cf~%60!%26tDX");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("tMi%0A_ao%3Bi8%3B(uQ6v%09S%09FMV%20%26'4C7l3ul2%2F4g6%3C%22%09%0A%C3%87ltyy%3Es!1E%C3%87%0A%5CQ", false, -803), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("w-nmV3X3My%5D%3A61n%23_l9t%5E%3EWvhGtY%5E47~l~%2Bno-0U67AFPhm7-n%2B%3FL1TZ%20a%60cx1Iy%3FH%20v26FMek%26%7Di%3BIbn(5lm%3BqwN6JiA%3FK%7B%7C%5EZ%25UKS*a'*%40JS%7Cz%2F", false, -7549), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("(m_Sp%2B%0ABXLxyE.%7BjezH%7DF%C2%A9%C3%87UDwzf14FT%601i9Q1k%7DuP%3D'**B%3BBE~r~6%7D%26%3Fh%0981%5Cd%09y5X%3C(%5C%5BMr%5C*bhNTY%26t9%0D%3A%2C'WeC33QKv%0AM", false, -477), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("z4l%3Co%5CORSPGIQ%7CIkWUR4y%237%2CE6n%40f3_sQ%40%24~Ujl4%5DIN%3E2S!p", false, 198), "r%2Cd4gTGJKH%3FAItAcOMJ%2Cqz%2F%24%3D.f8%5E%2BWkI8%7BvMbd%2CUAF6*Kxh");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("GQ%26I_%2Bil*4%22JYf%3F%3D%3Ab%2BO%C2%A99%C3%91d%3C%09%C3%87Y72%5DN%40%2B%C3%91%09O%3B%5DTF%7CnRrk%0DE%26%0A%3C%7Bg%09%22y1BP%C3%8713Ax%5B%23", false, -15), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%2382x~%60%3BI5Yue!jE(-%2B!TZsHZo%25dQ6~%25%60%23%3BEtSam%5D~*H%40%5EKK7%22pRKcT(%24Ml4xCGQ)zU%5Ez(f7VY%22d%23%5EP%26hz)%3FWE7%26'h9zPV", true, 812), "WlfMS5o%7Di.J%3AU%3Fy%5Ca_U)%2FH%7C%2FDY9%26jSY5WoyI(6B2S%5E%7Ct3%20%20kVE'%208)%5CX%22AhMw%7B%26%5DO*3O%5C%3Bk%2B.V9W3%25Z%3DO%5Ds%2CykZ%5B%3DmO%25%2B");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%5E%22odnUL.%3C~u.%3A1M7C%2BL%5EQ0%20o%25%5Eet%25R%3FP0%3D%3A%2BA%25i%5Bt%7CY%3Aj%3DbX%24ZyEW'%3AG%3AB7dhN1n%5DxFmS'rFdix%23C*lg%7B%7B%24%22)ZTr%24%23LoRRSCYA8pBI%3Ba%7D6wo%20kv%60%40Q1Vo%40HNo%5D%3B0c3g(", false, 1862), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("5q%7C971baX4%3E7'wf%60CQdz%25%3Ed%7DHi%3C%606OGs%24OaH)%3Bg%5D%2BvHpmd*Z%2B%60XQfM%25V%2Bh2Fw'%5D%26m-%23_%5C%3DJYD(Z46%3FDRJbGI%5C%25%3DKNjMdTI%5CA*%24%3AEE9~WQJn", false, -4524), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("CF25WJOD%20My%5Cn0dW%3DdAI!oQQ%2F%23%20CkVP%7CFs%22%266L%40_%3E_vlKi%24)B%3Dv%603Op_-Ok%2C)%40*7%2By%5C%24FpVMvbGWS%5B%7CT", false, -204), "QT%40CeX%5DR.%5B(j%7C%3EreKrOW%2F%7D__%3D1.Qyd%5E%2BT%2204DZNmLm%25zYw27PK%25nA%5D~m%3B%5Dy%3A7N8E9(j2T~d%5B%25pUeai%2Bb");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%22a%3E~)%3D%3C_EoZ.-q*kOKNA%26P%3C~6a%60%40-pt%3Dg%3FXI-0_Xi9oi%7B%2BH%3BH7!AY%5Bf8%3Cstz_M%3D%3Aa%5E%2Bb%7D%60R*%7B5xT%3C~%23!%5CtnJ_N%7CH4M%5C%25IulU%5D%3BFS%40_3%2B.", true, 500), "%3BzW8BVUx%5E)sGF%2BC%25hdgZ%3FiU8OzyYF*.V!XqbFIxq%23R)%235DaTaP%3AZrt%20QU-.4xfVSzwD%7B7ykC5N2mU8%3C%3Au.(cxg6aMfu%3Eb%2F%26nvT_lYxLDG");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%5CbvI%24Q~BTSr%C3%91aj0f*Q'v%7C%09jRx%C3%87%40lE%3B6S_%3D%2C9cxx%7C%5D%3A%5BI%0A%3EE%C3%91f%3B%20%3C!7%40U%3Die1%7B%24~J%5E%3DNAffg5)R5ZZ(3OL'u2Kgy%3Fz%C2%A9C-%60%09%3DjU%23el%20dYgFoSJiC1b%3BY%0AJ", true, -763), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%25U%7BINeg(%3D*c.k2-%3D%221vmaR*%7CCD%7DctaaXwTt%23.yZ~%22y8zm%5CGWs%40%7DCc9x*!x02%3AwD5P%5C%25QsJ", true, -1964), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%60%0Alg%3B%C3%91T_2%09G%2F%5B%C3%87my4%2BRwo%26z7-'i%098n3%3CP%2B8%093A%0D%22%3E%C3%91v%5B%09%25%224K!b%5CN8qw!%C2%A9Lc%3A%09%26p3%60", false, -172), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("dg8Jw3n%7C%60%0D%3D%7DK2%3CC%3B%2FIZ%09%C2%A9!%26m%3Cn)M-)-(vLO4%20zUgc%7BQ%5B%5E%09BD1%40%60%20_%60sOA%2C%5B~-%C2%A9%60)Z%23%0AOx%0Dl4R3t%09%60", true, 323), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("gtJemJw)Q%5C%20agvw(R6n*3-YZk%40%23483%3B(%20%2F1%232z0)%3B%26wjexg", true, -365), "v%24Yt%7CY'8%60k%2Fpv%26'7aE%7D9B%3ChizO2CGBJ7%2F%3E%402A*%3F8J5'yt(v");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%231KDW-gk*iCx%5Dk7Q%23cPDj11z!GQ%5E%2B0U1%3FvF5c6k%24%7C%23uZM9x%60.sDA.%24%23J%3DF%5EZ%2CI)%5DFo%3D%7DoGCM4%22J2wb(%40vs", false, -957), "*8RK%5E4nr1pJ%20dr%3EX*jWKq88%22(NXe27%5C8F%7DM%3Cj%3Dr%2B%24*%7CaT%40%20g5zKH5%2B*QDMea3P0dMvD%25vNJT%3B)Q9~i%2FG%7Dz");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("lD)R%22Xh%26%3FY%60T4%5DCC2*HWs6mN%25%7BRJSs%7B%3C-E%25Gmu%3Ee_8b7v%40_*vUD%5C%7Bnm%3F%2B%2BO%2FK%7DM%60mZz9SSdoPmj%2B1%40%3BY%25Abq%23%40DJNu8G7X*!Zl%2Bx6%2F6kvseW7Q!%2C5Mpo%7C", true, 4725), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("k%25j_tPfv%0At%60ZY%5EQw%20*%20d%C3%87%3EV9q%5BWl%7B%22%3C%7Dst%3E%5Bx)V-VB%7D-qq~%3Cv3ZFvCR3%2FvM(o%2F%25r%5EEk7%3FU%3A0W%5E)d%2Cve%2BlLMHJZSR%40hwcuI9%3ARiZn%3F", true, 470), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("PpZ%23m'%20%5DiN%3Dpz%3AYTW%7D%3AnIS%3DuHpw%3B4%3ETSN4%3AD%229%3D%3BeZy", true, -2945), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("l%C3%91-%7DlZBv%0Dp-%C3%91h2v%C3%916%0A%094l%23%C3%87'W6.s%25%09e%0D%C3%91%2C%C2%A9%0Ao%C2%A99", true, -655), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%2Cj0ki%3Dq%5E%5C9%2570%22%23nr)V7CnklJN7%7Bh%25%23_tH%26ej35bb-4%23Q0mt.L%3Bus%3EtVSY9eHI%3C%5DSe!y*%22G%5B%20dGleu%5CYN~f2YF2N%20q14SxqG", true, 73), "uTyUS'%5BHF%23n!yklX%5Cr%40!-XUV48!eRnlI%5E2oOT%7C~LLv%7Dl%3ByW%5Ew6%25_%5D(%5E%40%3DC%23O23%26G%3DOjcsk1EiN1VO_FC8hP%7BC0%7B8i%5Bz%7D%3Db%5B1");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("YxX%5B'e%2FV%5B7IH%3DI%24YC%C2%A909f%2By-k02%5EffaYwN7Eg3%2F%5B", true, 854), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("fZqi4)Xrw%20%7Br5%7C%2BeY.e%40n!c*0VI)%26%26HARvcI%3A%24~k%3C%3BV%3E%25QVnugaoCc42%3E%23I%7BYV%25C%5B%5BfJf~KJyjds%60A3ILRpFU%204%26%60w5eq%40KxFa%3Buk%3C%25Vrq8%25Nux.wZE%2B", false, -788), "%23v.%26PEt%2F4%3C8%2FQ9G%22uJ%22%5C%2B%3D%20FLreEBBd%5Dn3%20eV%40%3B(XWrZAmr%2B2%24%7D%2C_%20PNZ%3Fe8urA_ww%23f%23%3Bgf6'!0%7C%5DOehn-bq%3CPB%7C4Q%22.%5Cg5b%7DW2(XAr%2F.TAj25J4vaG");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%C3%91bN%09s%C3%91%26%09)%3F%3Cb%C3%87w!%09%0DbFEl%3FQ%C3%87b%09c%C3%91V%256T%25%C2%A9%2B", true, -985), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("niXE%3F%7DR!9~L3!Lx%24Uz8%7D~C74NQI%7DzD%3DTh%3CvAz%7BC%2C%220wY63%2BX-mg%3A%5CIPrRuB%2F%23%3DD%3EmX%269ci%2BVqVg%24-u%2C)", true, 740), "ZUD1%2Bi%3El%25j8~l8doAf%24ij%2F%23%20%3A%3D5if0)%40T(b-fg%2Fwm%7BcE%22~vDxYS%26H5%3C%5E%3Ea.zn)0*YDq%25OUvB%5DBSoxawt");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("6ea_%3A%5B5%3BX%2CY!D%7C*!hPevH%60zP4Fh*Cv%3E8Q%5DtO%5C%7Cr-O%3BUKsgT%25q~sUkyc%3CQ%5B%24Ochn%3F%2F%5B%60~jee%5CbOQP%40%60!7tv%5D4%2C%3D%3D%23%3DceHw.r%25c%3F5Tk%2B%23)S%3AsQ.1", true, 7373), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("Uo.Y%3A7t.WjB%7B%5C8u)vgy%22Va%5E%3D-fAC%3Bp2s%2F%246KopS8a%5CV~(%22Bu51", true, 10064), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("pB2%3Ep9yeQMpO(*Ml%24BR1pMwV~s%5EUh%60%5D%2B%3E%3C%23S%23%40%60Bi%22nv8q%266%60%20ZXNjt8y-A%2B", true, 7450), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("TIYbWGu%5DyCg%3CR6%5EfUJK-0SgWN9pm%0D%26(g%09e%5C1A3h%5E%2BV%22rztsVF%09%5D%3F(%23-t%60%227%7B5DI%2Ff_%22%2Coy%7C9%5E%24l%7DWqZA)%60)%C3%91g_%2BpY%5B%20*D.T%3E2%0Ac1%3B%3Fxix%60s50Aj%3CK_%2CkLr%3Bx1X%2C%C2%A9e", true, 818), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("EN!n5y%7DJp8RHU651YRr.%3FCDFEeVV%40gY-Cy._ghLCsn*%5EHW%7Dlte%2BLZ2RC%5B%200Q%3A6XU3~-(nt6F%20H%22%3ANpwhr", false, -6716), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("8cB!sq0U2EE1)4M%5ES5zx6.nNVSS7U%3D_Mjda)%2FsjQR%262H*'5WW%20UMVfI_Hzhjcs%2FHP%5CS%5BGd(%5Bn%3DI%5EBUFNstU%5DEC%3DTd'%7B%24%5D%5E90jJ48Cb%25BQ6n%5D*uehM(D)nn", true, -908), "b.lK%3E%3CZ%20%5Coo%5BS%5Ew)%7D_EC%60X9x!%7D%7Da%20g*w5%2F%2CSY%3E5%7B%7CP%5CrTQ_%22%22J%20w!1s*rE35.%3EYrz'%7D%26q%2FR%269gs)l%20px%3E%3F%20(omg~%2FQFN()cZ5t%5Ebm-Ol%7B%609(T%4003wRnS99");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("q%20%7D%5C%5BY%3ANPGl4(%7D!IC%5DB(%7D!x%24%23%24h0%5BGk5YL%5B%7DDZ%3D%2F%23%25U%2B5SsLj%3BC5ipu%3F'%5C7yCZBej2%3CGPaFt2%60N1JqfR7qWJ%7CzF*LZEGH*", false, -4660), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("Huu%3B%7Cuib6.J%3F.y%40%24%3CduRUcm%3A%60okN%3Cn1%24rJ.", false, 357), "_--R4-!yMEaVE1W%3BS%7B-ilz%25Qw'%23eS%26H%3B*aE");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%24Q_%3E%3A)d%22%20'M.KaV1gWvJ%3FZH(lql0'P%229b0%5EfW%267wd%7B%40%23r%7De(ekOaY%3Ed%5BY%5Cg0Ua'v!ujIPO%60N%20V8%24oN!Rxj%5D%3DG%24~9%5DA%3D)%2B'q%3E7I6B%23d%7DC%2FX%3CC%7D%0DjY%5E3T8o%3EfD%3CjW", false, 666), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("f%5E%236w4%2C''h%3A%20o3%7DI%3C--m%3Dn%5B*Hk-%3EQq%3FHp%3EDroT%7DVc%3Bbzy%60AVOEuJ%3Agu%40%3Etx8%5C%23eybehE)k6alFJ%3C%3D", true, 7438), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("xYhs80%40_%3EbqLBPm%264Bm%3FQ%2CXn%7C_C%22c0%5C4A", true, 246), "Q2ALphx8v%3BJ%25z)F%5ElzFw*d1GU8%7BZ%3Ch5ly");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("d%3B%C3%91E%0A8zf%20*x%26E%5C(%24bix.%5E8oJ%20%C2%A9%2Cqruv-%7C%60%C3%91mD%3F2(42%5E.%25%0A%3CVBYJU%3A%2BM24_pD%C2%A9j9%40E%3F%5CH1E", true, -578), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("WY%09XeJSSz%C3%91%60()6Qe((B%09O%3A%2F%C3%87%2F%2C%3Af%0DgKzW%22%0A%3A)%3E%7D%2F2M3%3ENC)zFid%09%5D7RHf%40%3F%C3%91%3B_l%3CY%2BPYJW%0D2J!Mp_%2B", true, -1012), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("qn%252ru.%3A%2ChSiEzA%3F%3EL%5Ei%20_%5ChVb(%5CE6.%5CQ9Dj)v%7C%3A-%3E%5ECNC%7Ci%2BtnfU%3FJYMj%3F%7CoL3*J%2CjXV%2CTgZPVj%3AESg9%3C%22o(nT%22p%3Bqxc%25BRYHr6%7C*Jm%2F-S%7Bd1xPPb%3F", false, 442), "30FS47O%5BM*t%2Bf%3Cb%60_m%20%2BA!%7D*w%24I%7DfWO%7DrZe%2CJ8%3E%5BN_%20dod%3E%2BL60(v%60kzn%2C%60%3E1mTKkM%2CywMu)%7Bqw%2C%5Bft)Z%5DC1I0uC2%5C3%3A%25Fcszi4W%3EKk%2FPNt%3D%26R%3Aqq%24%60");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("imT%2Br65oYOMjaC%3F%22LG~%3E-%3FCu8TE'Q9meD%3FP%225D_4Y3*du*6i%2BXPL%5BXgcP%7Cq%3Dp%7CYqh%5Cc1sk%5E~r~j'VeD*Ct%2FtiR%5B%3E%40%3A%5C", true, -4491), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("K%5Cc%3Dh%3E%3BGY%7Be-CU%24q~%C2%A9x%3C3%24%C3%91%C3%91%5B_%3D-Wx%C2%A9l%22q%C2%A9b%C3%91q)%09%7CZ", false, 600), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%5E*%2F%09%25%7B%40lL%0A%C3%87j%0A2%2B%0D%23!hU%C3%87Cu)UPmpp%23%0A%3F%C3%91%3D~jN~%3E%5Dc9%3A%09s8%7B%23JFL7W2T%C3%87XTUP%0DM0)r%5DAt%60tR%2F%22Vxj%7C%C3%91OL%3B0%7C3*%0D2oE%3Ag3%3E2V%23%20OM2zV%25!%20og%3DK0Qc%2F~", true, 265), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("VE3~2%3F3%5B%2F%7C1%7DrdH%2CZ10%24'%40FcK%3CT%40%5E%3CL~o3J%5C%3Dnx(%3Di71y5%3C%2FKoD%407%222%24)A3B%2Bod)nn%3Eo5pimV)Za5%3A%5D%7BZ", false, -5532), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("v%40zz6!5g_X'.(%3Fun%7D0%26jnfYEI%5Dm*Uf5HdLkDVKvW)wZ5lA!D685%7Dp%26yxyf%3Cv0%5E3D%5DRZ~%239_%20%5Dz2%20hE8gwf%24%2FiPmLh%5D%7Cf", false, -3872), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("_j%60%0A%3C%C3%91K%C3%91%20%5E_%22az_LI%C2%A9%0DA%2B1im%2F%3F0%0DQ%40.%3D%40i%0A%3C*J%200%0Afdx%26W%5DY%3AUQ%7D0r%C2%A9%26%C2%A9Zmgs%C2%A9", false, -428), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("DJ%3E5qYEWOfJhu%25r%5B%5B2%3C%5C(gP%3FxFKK5%7Ck%7Blxt18%22M%2C%5D", false, -9169), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("ibO%60sJezy9A%22y6Z(%40qG%22O12_6kGl5%5B%40l!%3CDNmmUi%3F%5BGU!t%3Exi.28ie%7Ce%5D%40EAnp%3C)Sm~!m!%3D5O%3EURj5o", true, -6284), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("wrNpj%2F1oMfZATd%2BQ%7B1SpDld!ED%40)XniuOn%25%3ESmK5r%3B%2F*tEEu'Udy%3DfST!%7B%3BgP%263tbcj~t_*%5BhunFp%23h%24U%25%7Dwwy%2BS!%5C!", false, 524), "FA%7C%3F9%5D_%3E%7B5)o%233Y%20J_%22%3Fr%3B3OsrnW'%3D8D%7D%3DSl%22%3CycAi%5DXCssDU%243Hk5%22%23OJi6~TaC129MC.X*7D%3Dt%3FQ7R%24SLFFHY%22O%2BO");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("S2_O1nP%60S%25d%24UR%25u)mSrs3cK2rt_4P%5Bd67!l-owH6m)V%239%3DtPJKExb52WFo%20%22m%40zas", true, -6068), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("Inp%23%3BvZb%22%40dDS%C2%A9Pqd%3CB%5CVGlcXUctao9%7BDR%C3%87xeF9)Rc%24LkQ~gQv%2CE-QS_Bq%3E%3F0%3B32%7DYmL%24%C3%91D3%3Emd~X%7Dtbt%3EaXC%5EL~T*%24l%5D5J%26nBQKJ5%23UVn1F.%2C%22J%3ALxhf%3AT", false, 568), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%7Df90p%3C.Z%7C%7DsCyc(%3B-3KuKRt*2B%2333!%3FkL*yT0%23hvq~%5D%239%605CID%24YWpWMqyzuNeoNhb3~N%3Ee%2B%7D%7D%2CSZ%5DTE3g%23%7C'%24(jm%40", false, 8829), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%5BGdQ7A-1%40h'%2FW~F0a%3AU~flv.*cy%3Ea%25BASMV'5%23j%3Fkd8%7DNZ'%7Bdj%20*%25%5De%5ES'FZmuDzeLkjzUq05pC%3E%7CW'8%24wrKCOUby(%2F_Bzz8%20ZEM)", true, -576), "UA%5EK1%3B'%2B%3Ab!)Qx%40*%5B4Ox%60fp(%24%5Ds8%5B~%3C%3BMGP!%2F%7Cd9e%5E2wHT!u%5Edy%24~W_XM!%40Tgo%3Et_FedtOk*%2Fj%3D8vQ!2%7DqlE%3DIO%5Cs%22)Y%3Ctt2yT%3FG%23");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("Q4ZqIR%2B%20w-%2F%2B%7D%60S%7C%3C%2FRme%3FCfyAfG1%5D.7M%5E%3BodbAr1r%7D%3A5'L%3AY%20%40-8k5zc6)lU_%3BdRP_%5BD-%7C%26%26)G8f*B3gg7X0%5C5NQM4G%25!%3FL%22GqQ%3CI%7Bz%2BMxB'%20vAL6ws%3B", false, 891), "-o6M%25.f%5BShjfY%3C%2FXwj.IAz~BU%7CB%23l9ir)%3AvK%40%3E%7CNlNYupb(u5%5B%7BhsGpV%3FqdH1%3Bv%40.%2C%3B7%20hXaad%23sBe%7DnCCr4k8p*-)o%23%60%5Cz(%5D%23M-w%25WVf)T%7Db%5BR%7C(qSOv");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("gb%C3%87u~IPzM5v%09V%5Cjv%3B.mP%0D%24PLn%24EVx%25qP%26%C3%91VE%5EI%7B%2F)4%09p%24d1p%22)F%3BX%2COU.GZ2%60!k'%3A)!*zx6H", false, 336), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("lGF%3B8%3D%2F%40wlYhG6%5B!9z7jUOr)MtzFV0%266E", false, 342), "3mla%5EcUf%3E3%20%2Fm%5C%22G_A%5D1%7Bu9Os%3BAl%7CVL%5Ck");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("m%40OGW%5CGN)o!H6~yu%5Ck)7%5Cbw~M~c%3Cnjzb%3Agec)7rCW", false, -889), "0bqiy~ipK2CjXA%3C8~.KY~%25%3AAoA%26%5E1-%3D%25%5C*(%26KY5ey");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%C2%A9%3Ew%C3%87%3Bbp%0A%25yN'M%20W%3B%0Dp%5DBzr%3F3%0AeT2%5D%7Ci4xOZ%3F8v2%0Dl%C3%87%5DGMzb%60%092%7D*!l%C3%91%2Fag3WcWMQ", true, -758), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("NmG%60~9%5E%3EQ%09DZ4FWY%25~h%5EHF4~I%C3%91(%5D%40%0Adx%3D%C2%A9%0A%7B", true, 977), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("iI%0D%C2%A91%09cvLon%09%7B%C3%91P%3A%25%5E%40%C3%91T3%3E3%0A7%C3%91o3i5%3DoZ%3EDr%5D%C2%A942HTJ%C2%A9%09N%0DhBcnc0%C3%870abbasy%2F%24", false, 448), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("5%C2%A9%C3%91r5E%09R%C3%87%0D%09f~Ow%09(e%0Dd%09S%20Jy6%C3%87F%C3%91UK%C3%87%20vH_j5F", false, -1001), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("!a%5DV'a-vBt%5EmY4s%24e%249%3B31%5B%5D~-3w%7Dkhz~zJ3%3F%5B-Y'lSe%40Bn.X9%3DH%7Bh3%2CVYIyT_eTFBQs(_g(%7BsM%5CbAcc3gN", false, -4337), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("pHZ%3FO%23%7C%60%2F%40aHMh(6_%25r%2Cr!9-P%3DK~YO.%23uG%23B9%22j0%5EoFmAx%3FucRbWua%3B%3Cqx%3A1bC!A~dlJq%3D%20LjyU6%3E%23p7%22%3F%23-asO%5E", false, -6842), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("Y6SKE%7BEs*.%3A%3Df%7B%2F5%2BH%25%40!gPn%3C%5D7KrTHp0%2Fg%2Bo6mr%3C.L%5BRyoi%26IA%3F%20%25%09-Mm%C3%87k.%5E!V%60u%7B%5BxQE%5CDmgVjm%09~%24.18", false, 142), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%5BbR(HFeustl7%3B)-G%26!KxF5O%3C%2CRC%24n7%5CYR%5CK9WC%3FwS%7Bm)n6%3EJueC%2F%26jlvFKIu%7DMa3%5ExMIn%7CTT%5B6tE%2F%2CC%5CCTwY'a%3A084%2CQM%23x%60.%7B%259CC%20~JE8%3FvoM8u%5Bc%5C", true, 8978), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("02%3B%258iE%5DQ3R%6008%3A.%26%5Cm2*t%3B4l.1rj3'Zl7(nx%22Q4rCnkgL%20ThB%3EdM%241!-8YPn8%22*%40d3snh%40bi*trc10u%7De%2BGa%60%3CUkq%24sk*%5Cym'%5B'No%2Cws%2B7%25", true, 372), "(*3%7C0a%3DUI%2BJX(02%26%7DTe*%22l3%2Cd%26)jb%2B~Rd%2F%20fpyI%2Cj%3Bfc_DwL%60%3A6%5CE%7B)x%250QHf0y%228%5C%2Bkf%608Za%22lj%5B)(mu%5D%23%3FYX4Mci%7Bkc%22Tqe~S~Fg%24ok%23%2F%7C");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("in7%3D%2CQ!%3AD*7R%24%20rQwq%3C%24ek%5CWbwL9En%3Fy%3Bl%22I!T%209%5By%7C)6~Rq4(hu%5EH%5B%3AZ%3A%5DP%7B%3D%3FpsfdD7Cy)!%20K%2F%3E%3E%3BD4C%25hh*QwRLN)n%3A%3Be8k", false, -458), "X%5D%26%2Cz%40o)3x%26Arna%40f%60%2BrTZKFQf%3B(4%5D.h*%5Bp8oCn(Jhkw%25mA%60%23vWdM7J)I)L%3Fj%2C._bUS3%262hwon%3A%7D--*3%232sWWx%40fA%3B%3Dw%5D)*T'Z");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("5%0Dx%09u4%3CzaVNL%0AxnM!%0A%0DH!M%C3%87%C2%A9%C3%91%3B%7C%3B%C2%A9%60%C3%87ZZ9%C2%A9)b%0A%C2%A9Q%09", false, -527), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%2C%40_N%24*1xm37KIBE%3BAUX3(N5S%3B%2FQ6e%2CT%25G%2CqG%25LCt%3CCZ%5Eu~%7BIV1%2B%3E.%208J%7D", false, 7067), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("hc%40TlWM%5C2X5%40aB4%25%3F%3E6x%2F%7DhfDZ%5EE%7BSud*MY%3CXKe*IpZ_J0B%40WE3DuQJdb%20DPmUY%2F~b%25lDczkA04p2%40iz%60jT%5BYR41c%23.%3E%3Ag*%25%5CvZi(T4.-vvw%7CyRDt%2FD", false, 3108), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("ZZipaP(i_%3Fg%5Bvf7rm*)%60d%259%3A%22qw~x%3BnH%20%26rJm9%25F%5EHBy%3EuNE)1-i%22M%60p%20P%5Eoxx%7BxK8IV%23OK%2CbC43", false, -870), "iix%20p_7xnNvj%26uF%22%7C98os4HI1!'.(J%7DW%2F5%22Y%7CH4UmWQ)M%25%5DT8%40%3Cx1%5Co%20%2F_m~((%2B(ZGXe2%5EZ%3BqRCB");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%22v%5D%7CbY%7D%7BojbT)%251Y%7Dg71_3'OnRZ%5CQx*vw%23-X%60D~V~f)jEz%3E%26%5C%20_SnOMY%5EQ%3FpA.H9%2FxveMxz0-*WJcA_%25%3E%7CD.%3Fl", false, 9580), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("sZT*PV%2F9UVD%23z*B%7Bx%3B(6%5CVHT%3AYIMEiiBf%7B%3Dy2x%3B%20o49ws%26k4'BnOL)%20'4T%2FTI4%20", false, 1816), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("SKjr-J~JQY0tYSBp%7B.D%26-EX_zVTw)%3FUa%7BlQcjkZ%5EILMK*%230EWL%3B%6033qepnZk6%23Nhg%5Ckymf4bT%20V%3DhiT%5DEU%3CA%24x4D..%3B", true, -9846), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("'L%5DP%24l.D%60UPn%20L'%2B_n5hw%25%3Fn94LhN%7BmyU4%7DcyI%3A)%3B-M%7Bf%260l%3AvV2'N*%40%24m%26%2F6%5E%5D_SDi%24%26k1XNgvpCie%5EbMB%20%60%25%3B%7D%3Ej)z*(J", true, 573), "*O%60S'o1GcXSq%23O*.bq8kz(Bq%3C7OkQ~p%7CX7!f%7CL%3D%2C%3E0P~i)3o%3DyY5*Q-C'p)29a%60bVGl')n4%5BQjysFlhaePE%23c(%3E!Am%2C%7D-%2BM");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput(")%2CfX%3Bc%3Bwoy%3EWCkTir-0e0m'S%24%60N6X%25Tz%2FqX%5D0%22%20z)%25%25m%2C05aeQft%5D%2CL5%25o%20hrhtVInJEV6dfP%7Dre1sf%3B%3BN_4T%5C9Zc!%3A4O%24", true, 801), "RU0%22d-dA9Cg!l5%7D3%3CVY%2FY7P%7CM*w_%22N%7DDX%3B%22'YKIDRNN7UY%5E%2B%2Fz0%3E'Uu%5EN9I2%3C2%3E%20r8sn%20_.0yG%3C%2FZ%3D0ddw)%5D%7D%26b%24-Jc%5DxM");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%407-(c%3C%3C*!sCHV%5E~f2%5B%7DsrCnwb%40t_%60%5BShu%5B%7Bd%5Ej%7DrR%40%5D0B%7C_3Vj%22%2B%3B%2Ffa%5D5tRPebdIG%7D47A36%7Cw%2Csxlkdr%3A%22%7D%2C%7Ba%3Ave%3Afb4z%40d82w%3FdKv0g", false, -727), "~ukfBzzh_R%22'5%3D%5DEp%3A%5CRQ%22MVA~S%3E%3F%3A2GT%3AZC%3DI%5CQ1~%3Cn!%5B%3Eq5I%60iymE%40%3CsS1%2FDAC(%26%5Cru%20qt%5BVjRWKJCQx%60%5CjZ%40xUDxEArY~CvpV%7DC*UnF");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("N%5D%238%0DeYEr%20Pk6YgC6'%3Cs%3EuO%5BX%7DGnz4%23Fk2dV%602rIV672%3A%3D%2CXJRw8ma8hCAZ_%3D9v%23F%C2%A9PF9R%7D7a0P%3BET0%26%C3%91", true, -806), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%C3%87N%0A2K)G%094x%0A0l%09%C3%91%5CI%C3%91n%09t%C2%A9w%0D5%24_%5DT%C3%87%C3%87h%09%40%3C%C3%91%2FGNCV%5D", true, -980), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput(")%3Fc%3Et%23Slo%3F%2Fdl%20jurSR%263Vqi6jjYH9%0D7xGe%7C5%22%C3%87%0DZw%7DclaD%40%5C%7BJYAchiH%5C", false, -787), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("waS6%22Dier1K(ozx%3B38mPL~%3F%5E%2B23iP8F%2BWQ_%22d%3A(OLr%3A(0)", false, -4954), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("N%22n.wE-%7B%3BK%5C-%23%2BAR%60nW-)1Eom%5B%25-_oZ%60q%3BW%22qN(%5D.7mA%22%3AoeY*%23ZIMOHK%60*%2B", true, 4859), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("s%23h%2BJDY%0A%C3%87DD5%0D8%23%5Bv%3F%2C%25l3-E8%5C%2FoN5%C3%87(_V4((S*AAI61%0D", false, -449), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("6wrJ*%2FdlV.8V%22(O%23O%2F%248S%409GacpcN%3DTb%25iE1U%26x%5Ca%3B9ToXWiXFm%40msn7OO%7B%261S%5DDx%3C6!h%7C.p%3B*wXqY%25iutU%3Cjw*%7C%60~J%22MCr%5Djm", true, -3754), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("M.%3AJ%5BO4%60o%3A%5CT'%5Bsz96i!nK7%23q%5Co%3Bt-LG%208SorNL%26%24BS%22E0D9T%5CitR30%22E%3Au!wjh9C.)%60Wea%5C%5B%265'lpfS%2F%3FNev%5DElPE%5B%7C%5Cyt", true, -3086), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("GU%26Iu%23%2053%7BkpYUP%22s7T(%3C%26%3DqK6%5EPW-v%3F%7B%5DwYGwLi%25*s!%3Ej_%3BQDww56%3BzLl9RkI%24%5BO%7CcgW!%2BerMqE%7CYt%5C%5B%2BP%3C'%3FToHyCZP%23", false, -6134), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("!H.%2F%20%5B%0D%23G%09Z%2FAl%2Bzn'G%5D%09%601PHn%5D%20%25EP_elOKt%C2%A9%0D_YRF0U%C3%87mv%5B%2Bx%26%C3%91F%C2%A9kN%40.Yux%7BAvQ%3D%C2%A9%C3%91w%3AnD", true, -972), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("Sd%3DGT%3AVHu5y%3C%248%5E%26r%7Ccy9OL%258WNdTfT-OD", true, -193), "Pa%3ADQ7SEr2v9!5%5B%23oy%60v6LI%225TKaQcQ*LA");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("wuyIK%09JA%60gt3yH)f-N7h)I%3E%7B%0D%2CD%C3%87%20%60%C3%87%40!%3C4)%C3%91AomfY%232e!E%60I%3E*.Zepe%2B46!6%5BHoKe4h0%3B9jG%3FkI%3Evko!Ku%C3%91zYNT8%5B%20ew3*i9~nj_EHqJ%25-", true, 869), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("R%2B~%7C9rj%5B%24%5B%22%7D~%25oVQ%7BIjbI%60x%22pQQI*b%40(dNq)Z*%5Ct.%5EIoVl51%5B-%7CB2RCJ%3D%5EyRb%7B%5C%3AL%5BM%7D%3Fjen%2FRiI%7CrSQ", true, 536), "0h%5CZvPH9a9_%5B%5CbM4%2FY'H%40'%3EV_N%2F%2F'g%40%7DeB%2COf8g%3ARk%3C'M4Jrn9jZ%20o0!(z%3CW0%40Y%3Aw*9%2B%5B%7CHCLl0G'ZP1%2F");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("9ki%09BB-B%20li%C2%A9_olNC6Y%0DU6%C3%87%091e%0ABdx3T-%0DD%7Bs0%C3%8707%C3%91g%09%C2%A9%C2%A9z%26DgV.c%C3%87geMW)N%24%09%C3%91", true, -114), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("Su%09_dbsV(%3EV%7D%2FC%7BIL*qd'%2679a%3AWcd%24UM%40PC%3D%0DQ%5DgvMjTq%24S3T%60%5EA%3FZR%25_dv*%2B)s%3B4Fw%0D", true, 438), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("MhQ%2B%5ES9k%2F%3Fhtc2%C2%A9rY4~n%7CZ%5Cn6%5B_%C3%913%22t2Axq%C3%91%40*T%C2%A9X%7C8%0At%3B%C3%872'9%3A%C2%A9%3Ae7%5E2JH%3EX2S%3B*SW%25%C3%874K%C3%87%60%0ANO%3AP%C3%87N)H%7Cn*EkoRFFfFGq%0Dw%3Cm2", true, -903), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%7D%3CeNU%24AnI%7CN~(hV%5E%5DFPm%2B9C%5E-43%25e%5Dw%7Bk8OVB%3D", false, 484), "t3%5CELz8e%40sEu~_MUT%3DGd%220%3AU%24%2B*%7B%5CTnrb%2FFM94");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("v%3EF6G_%40'%3Ec5%5D%C3%87vue%23%C2%A9!3S%20eQZ%40Yn%2FqP%2Fm_yR%7BB3%3BRhqe!%40%3F%40%20o%3Fdw%5Dt(VYRgy%C3%916%0DhRw%C3%87nb%7B_%24-Z1)HqQb%20xbgV%095%26%2B%0D1%24IM%0ALA~q~hCI)HDO)%24%2C%22n2OY%0D8%3F%7C2P%3A*N23%0A", true, -849), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("q%C2%A9%C3%91Y3_klmzK%20t%2B%0AYzaR%5B%0D8vyMS%0DLi%23R%3C-'0.CJI%3CPsMQL4%22%60%23vFa%3B%3BM%2FJ%0D%C3%87h%5E0T%7D%2C80AONl(ZF_eyrHKT%604S%20a%C3%91SeH%40g!sc%20%24w%C3%87%C3%87Ou6%C2%A9McXN%5D", true, 782), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("HaRD3*u%7C%5Bgk%3E%5B%25kwl2BvgF%2FQlQ%236r%7BJeTNSZ%5B%23dF%3Ee6W%23%2C%2Fu%7D%2FjqyWR.Dcg%7Cg6t%60A~2oDp%3CnRsPxZ!)2GV2n", true, -77), "ZsdVE%3C(%2Fmy%7DPm7%7D*~DT)yXAc~c5H%25.%5Cwf%60elm5vXPwHi5%3EA(0A%7C%24%2Cid%40Vuy%2FyH'rS1D%22V%23N!d%26b%2Bl3%3BDYhD!");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("X%22Y1_%22qX%3F%20%09%3BFo%3F%C3%91FOJOY%C3%87N%3AF.%0A%223ob%40%26kI%60u%5BR%26n2E%C3%91%3C9)5E%C3%87.%3F%3E5fES0%0A%26.%26mPJyXjJ%5ByZx5P%3Fh*%C3%87%3EgQd%C3%91dN%7CanV8)%5B%0D!%5E%60", false, -349), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("%7D%7C%3Ay-l%7D~!-%7DB3xeIZ%5Dx4%7D%2B%237I%40T%2B%3DwOW%24dq0%7BP%24%26'%26%3DH%7Bs-IIkjbr%24iODW%24~7", false, -8429), null);
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("XK%24.nCH%5E0%23_U%3C%7DbX'%240TtvD%24S%5B%3Fw%7D%22%3EzD*v%3FSC%5B%2FV2as%3F%5E", false, -602), "xkDN%2Fch~PC%20u%5C%3E%23xGDPt57dDs%7B_8%3EB%5E%3BdJ7_sc%7BOvR%224_~");
        EncryptTest.PRECOMPUTED_RESULTS
            .put(new EncryptInput("trmE*JeQ%23r-oRZomf-k%25%24eec)S%7C%257xsi%5CgO3%3F-tX6-mA%3F01(Z%22BOB%20qrsjy%20%2CE%22-4rqO%3B%23%248j)jva%7B%2C%24u-g(zZT", true, 7755), null);
        /* END AUTOGENERATED CODE */
    }
}
