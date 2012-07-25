/*
 * Copyright 2012 QitSoft LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fixtures;

import com.qitsoft.qitchain.annotation.ChainListener;
import com.qitsoft.qitchain.annotation.ChainListenerType;
import com.qitsoft.qitchain.annotation.ChainListeners;
import com.qitsoft.qitchain.annotation.ChainStep;

/**
 *
 * @author Serj Soloviov <serj@qitsoft.com>
 */
@TestChain
@ChainListeners({
    UnnamedAfterListener.class,
    UnnamedBeforeListener.class,
    UnnamedBeforeStepListener.class,
    UnnamedAfterStepListener.class,
    UnnamedAbstractListener.class,
    UnnamedBeforeListener.class
})
public class TestChainObject {
    
    @ChainStep(order=4)
    public void step1() {}
    
    @ChainListener(type= ChainListenerType.BEFORE)
    public void beforeListener() {}
    
    @ChainListener(type= ChainListenerType.AFTER)
    public void afterListener() {}
    
    @ChainListener
    public void unknownListener1() {}
    
    @ChainListener(type= ChainListenerType.UNKNOWN)
    public void unknownListener2() {}
}
