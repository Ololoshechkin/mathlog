//
//  main.cpp
//  mathlog
//
//  Created by Vadim on 16/05/2018.
//  Copyright © 2018 Vadim. All rights reserved.
//

#include <iostream>
#include <vector>
#include <string>
#include <sstream>
#include <functional>
#include <unordered_map>
#include <algorithm>
const int MAXN = 105;
int n;
std::vector<int> g[MAXN];
// a + b = min(c >= a, c >= b)
// a * b = min(c <= a, c <= b)
// a (b + c) = ab + ac - дистр
// a -> b = min(c | ac >= b) - импл
// bool algebra = ?

template<>
struct std::hash<std::pair<int, int>> {
    size_t operator()(const std::pair<int, int>& p) const {
        return std::hash<int>{}(p.first) * 31 + std::hash<int>{}(p.second);
    }
};


class C {
private:
    struct MyPair {
        int first;
        int second;
    };
    friend struct std::hash<MyPair>;
    std::unordered_map<MyPair, int> mmp;
public:
    int x = 0;
};

template<typename MyPair>
struct std::hash<MyPair> {
    size_t operator()(const C::MyPair& p) const {
        return std::hash<int>{}(p.first) * 31 + std::hash<int>{}(p.second);
    }
};

C c;

std::unordered_map<int, int> order_pos;
std::unordered_map<std::pair<int, int>, int> sum;
std::unordered_map<std::pair<int, int>, int> mul;
std::unordered_map<std::pair<int, int>, int> impl;

bool leq_impl(int a, int b, std::vector<bool>& used) { // a <= b => a <= .. <= b
    if (used[a]) return 0;
    used[a] = 1;
    return a == b || any_of(g[a].begin(), g[a].end(),
                            [b, &used](int v) -> bool {
                                return leq_impl(v, b, used);
                            });
}

bool leq(int a, int b) {
    std::vector<bool> used(n, false);
    return leq_impl(a, b, used);
}

bool geq(int a, int b) {
    return leq(b, a);
}

void calc_mul_sum() {
    for (int a = 0; a < n; ++a) {
        for (int b = 0; b < n; ++b) {
            for (int c = 0; c < n; ++c) {
                if (geq(c, a) && geq(c, b)) {
                    if (!sum.count({a, b}) || leq(c, sum[{a, b}])) {
                        sum[{a, b}] = c;
                    }
                }
                if (leq(c, a) && leq(c, b)) {
                    if (!mul.count({a, b}) || geq(c, mul[{a, b}])) {
                        mul[{a, b}] = c;
                    }
                }
            }
            for (int c = 0; c < n; ++c) {
                if (geq(c, a) && geq(c, b) && sum.count({a, b})
                    && !leq(sum[{a, b}], c)) {
                    sum.erase({a, b});
                }
                if (leq(c, a) && leq(c, b) && mul.count({a, b})
                    && !geq(mul[{a, b}], c)) {
                    mul.erase({a, b});
                }
            }
        }
    }
}

void calc_impl() {
    for (int a = 0; a < n; ++a) {
        for (int b = 0; b < n; ++b) {
            for (int c = 0; c < n; ++c) {
                if (leq(mul[{a, c}], b)) {
                    if (!impl.count({a, b}) || geq(c, impl[{a, b}])) {
                        impl[{a, b}] = c;
                    }
                }
            }
        }
    }
}

int pr(int v) {
    return v + 1;
}

template <typename F>
bool check_op(std::string&& op_symbol, F pred) {
    for (int a = 0; a < n; ++a) {
        for (int b = 0; b < n; ++b) {
            if (pred(a, b)) {
                std::cout << "Операция '" << op_symbol << "' не определена: "
                << pr(a) << op_symbol << pr(b) << std::endl;
                return false;
            }
        }
    }
    return true;
}

bool check_distr() {
    for (int a = 0; a < n; ++a) {
        for (int b = 0; b < n; ++b) {
            for (int c = 0; c < n; ++c) {
                if (mul[{a, sum[{b, c}]}] != sum[{mul[{a, b}], mul[{a, c}]}]) {
                    std::cout << "Нарушается дистрибутивность: "
                    << pr(a) << '*' << '(' << pr(b) << '+' << pr(c) << ')' << std::endl;
                    return false;
                }
            }
        }
    }
    return true;
}

int get_one() {
    return impl[{0, 0}];
}

int get_zero() {
    int z = 0;
    for (int a = 1; a < n; ++a)
        if (leq(a, z))
            z = a;
    return z;
}

bool check_bool() {
    auto one = get_one(), zero = get_zero();
    for (int a = 0; a < n; ++a) {
        if (sum[{a, impl[{a, zero}]}] != one) {
            std::cout << "Не булева алгебра: "
            << pr(a) << "+~" << pr(a) << std::endl;
            return false;
        }
    }
    return true;
}

std::vector<int> read_ints() {
    std::vector<int> res;
    std::string s;
    getline(std::cin, s);
    std::stringstream ss;
    ss << s;
    int v;
    while (ss >> v) {
        res.push_back(v);
    }
    return res;
}

int main(int argc, const char * argv[]) {
    freopen("input.txt", "r", stdin);
    freopen("output.txt", "w", stdout);
    n = read_ints()[0];
    for (int u = 1; u <= n; ++u) {
        for (int v : read_ints())
            if (v != u)
                g[u - 1].push_back(v - 1);
    }
    calc_mul_sum();
    if (!check_op("+", [](int a, int b) -> bool {
        return !sum.count({a, b});
    })) return 0;
    if (!check_op("*", [](int a, int b) -> bool {
        return !mul.count({a, b});
    })) return 0;
    if (!check_distr()) return 0;
    calc_impl();
    if (!check_op("->", [](int a, int b) -> bool {
        return !impl.count({a, b});
    })) return 0;
    if (!check_bool()) return 0;
    std::cout << "Булева алгебра" << std::endl;
    return 0;
}
/*
 5
 2 3 4
 5
 5
 5
 5
*/
