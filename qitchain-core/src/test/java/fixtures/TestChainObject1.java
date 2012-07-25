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
import com.qitsoft.qitchain.annotation.ChainStep;
import com.qitsoft.qitchain.annotation.ChainSteps;

/**
 *
 * @author Serj Soloviov <serj@qitsoft.com>
 */
@TestChain
@ChainSteps(steps={UnnannotatedChainStep.class, UnnannotatedChainStep2.class, UnnannotatedChainStep.class}, order=2)
public class TestChainObject1 {
    
    @ChainStep(order=1)
    public void step2() {}
    
    @ChainListener(type= ChainListenerType.BEFORE_STEP)
    public void beforeStepListener() {}
    
    @ChainListener(type= ChainListenerType.AFTER_STEP)
    public void afterStepListener() {}
    
}
