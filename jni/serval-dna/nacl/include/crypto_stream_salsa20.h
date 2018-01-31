#ifndef crypto_stream_salsa20_H
#define crypto_stream_salsa20_H

#define crypto_stream_salsa20_ref_KEYBYTES 32
#define crypto_stream_salsa20_ref_NONCEBYTES 8
#ifdef __cplusplus
#include <string>
extern std::string crypto_stream_salsa20_ref(size_t,const std::string &,const std::string &);
extern std::string crypto_stream_salsa20_ref_xor(const std::string &,const std::string &,const std::string &);
extern "C" {
#endif
extern int crypto_stream_salsa20_ref(unsigned char *,unsigned long long,const unsigned char *,const unsigned char *);
extern int crypto_stream_salsa20_ref_xor(unsigned char *,const unsigned char *,unsigned long long,const unsigned char *,const unsigned char *);
extern int crypto_stream_salsa20_ref_beforenm(unsigned char *,const unsigned char *);
extern int crypto_stream_salsa20_ref_afternm(unsigned char *,unsigned long long,const unsigned char *,const unsigned char *);
extern int crypto_stream_salsa20_ref_xor_afternm(unsigned char *,const unsigned char *,unsigned long long,const unsigned char *,const unsigned char *);
#ifdef __cplusplus
}
#endif

#define crypto_stream_salsa20 crypto_stream_salsa20_ref
/* POTATO crypto_stream_salsa20_ref crypto_stream_salsa20_ref crypto_stream_salsa20 */
#define crypto_stream_salsa20_xor crypto_stream_salsa20_ref_xor
/* POTATO crypto_stream_salsa20_ref_xor crypto_stream_salsa20_ref crypto_stream_salsa20 */
#define crypto_stream_salsa20_beforenm crypto_stream_salsa20_ref_beforenm
/* POTATO crypto_stream_salsa20_ref_beforenm crypto_stream_salsa20_ref crypto_stream_salsa20 */
#define crypto_stream_salsa20_afternm crypto_stream_salsa20_ref_afternm
/* POTATO crypto_stream_salsa20_ref_afternm crypto_stream_salsa20_ref crypto_stream_salsa20 */
#define crypto_stream_salsa20_xor_afternm crypto_stream_salsa20_ref_xor_afternm
/* POTATO crypto_stream_salsa20_ref_xor_afternm crypto_stream_salsa20_ref crypto_stream_salsa20 */
#define crypto_stream_salsa20_KEYBYTES crypto_stream_salsa20_ref_KEYBYTES
/* POTATO crypto_stream_salsa20_ref_KEYBYTES crypto_stream_salsa20_ref crypto_stream_salsa20 */
#define crypto_stream_salsa20_NONCEBYTES crypto_stream_salsa20_ref_NONCEBYTES
/* POTATO crypto_stream_salsa20_ref_NONCEBYTES crypto_stream_salsa20_ref crypto_stream_salsa20 */
#define crypto_stream_salsa20_BEFORENMBYTES crypto_stream_salsa20_ref_BEFORENMBYTES
/* POTATO crypto_stream_salsa20_ref_BEFORENMBYTES crypto_stream_salsa20_ref crypto_stream_salsa20 */
#define crypto_stream_salsa20_IMPLEMENTATION "crypto_stream/salsa20/ref"
#ifndef crypto_stream_salsa20_ref_VERSION
#define crypto_stream_salsa20_ref_VERSION "-"
#endif
#define crypto_stream_salsa20_VERSION crypto_stream_salsa20_ref_VERSION

#endif
