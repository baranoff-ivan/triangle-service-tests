# Triangle Service Found Issues

1. Specification doesn't contain description for POST and GET response payloads. It is a bug itself, because we don't actually know how payloads should be represented.

2. Specification doesn't contain description for perimeter and area APIs response payloads. It is a bug itself, because we don't actually know how payloads should be represented.

3. Non-integers are acceptable for create triangle API. It is not written in the specification and example payload contains only integers.

4. Request payload format for create triangle API is not consistent with response payload format. It is somewhat strange behaviour and needs to be clarified.

5. Create request payload with negative numbers is acceptable. Expecting Unprocessible request error.

6. Create request payload with more than 3 numbers is acceptable. Expecting Unprocessible request error.

7. Create request payload with a + b = c is acceptable. It is a corner case there triangle turns into line segment and this behaviour needs to be clarified.

8. Create request payload with 0 length edges is acceptable. This behaviour needs to be clarified.
 

---
**NOTE**

I've not written canonical bug descriptions trying to be short.

---

