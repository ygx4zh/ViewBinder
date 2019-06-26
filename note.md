#### FAQ

1. 为什么Butterknife 被BindView注解的字段不允许使用private修饰

原因: butterknife的原理是在编译时根据注解自动生成一个类, 里面自动生成了findViewById的代码, 被注释成private后, 这个类中无法直接调用该成员变量赋值;
反射获取成员变量也是可以的, 但是消耗性能, 不推荐;

PS: Butterknife 一开始是在运行时采用反射的方式进行id和view的绑定, 后面有人反馈影响性能, 所以改成了自动生成代码的方式