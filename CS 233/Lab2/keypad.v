module keypad(valid, number, a, b, c, d, e, f, g);
   output 	valid;
   output [3:0] number;
   input 	a, b, c, d, e, f, g;
   wire abc, def, bg, abcdef;
   wire ad, bd, cd, ae, be, ce, af, bf, cf;

   or o_valid(valid, abcdef, bg);

   or o1(abc, a, b, c);
   or o2(def, d, e, f);
   and bg1(bg, b, g);
   and abcdef1(abcdef, abc, def);

   and ad1(ad, a, d);
   and bd1(bd, b, d);
   and cd1(cd, c, d);
   and ae1(ae, a, e);
   and be1(be, b, e);
   and ce1(ce, c, e);
   and af1(af, a, f);
   and bf1(bf, b, f);
   and cf1(cf, c, f);

   or num0(number[0], ad, cd, be, af, cf);
   or num1(number[1], bd, cd, ce, af);
   or num2(number[2], ae, be, ce, af);
   or num3(number[3], bf, cf);



endmodule // keypad
